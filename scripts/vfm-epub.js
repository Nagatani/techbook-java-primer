/**
 * 出力先（BOOK_TARGET）ごとの設定と、電子書籍（EPUB）向けにHTMLを組み替える処理。
 *
 * 出力先は環境変数 BOOK_TARGET で選ぶ（vivliostyle.config.js が読む）。
 *   pdf   通常のPDF（既定）
 *   kdp   Amazon KDPのペーパーバックの本文PDF
 *   epub  電子書籍（KindleなどのEPUBリーダー）
 *
 * PDFのテーマは、節番号（「5章 - 3」）とコードの行番号をCSSの生成内容（::before）で、
 * 脚注をVivliostyleの float: footnote で表示している。EPUBリーダー、とくにKindleは
 * これらに対応しないため、EPUBでは次のようにHTMLそのものを書き換える。
 *
 * - h2の頭に節番号の span.section-label を入れる（数え方はテーマの section-counter と同じで、文書の中のh2の順番）
 * - 脚注（span.footnote）を、本文の番号のリンク（epub:type="noteref"）と、文書の末尾の
 *   aside（epub:type="footnote"）に分ける。Kindleではリンクを押すと脚注がポップアップする
 * - コードの1行（span.code-line[data-line]）の頭に行番号の span.code-line-number を入れる
 * - 図（images/diagrams/*.svg）をPNG（images/epub/diagrams/*.png、scripts/epub-assets.js が作る）に替え、
 *   紙面での幅を本文の幅（132mm）に対する割合で指定する。Mermaidの図は文字をforeignObjectで描いており、
 *   KindleではSVGのままだと文字が出ない
 * - 表のセルの align 属性を text-align のスタイルに替える（EPUB 3では align を使えない）
 */
const path = require('path');
const { layoutForSrc } = require('./diagram-size.js');

const TARGETS = ['pdf', 'kdp', 'epub'];
// 本文の幅（mm）。図の幅を本文の幅に対する割合にするときに使う
const TEXT_WIDTH_MM = 132;

function currentTarget() {
  const target = process.env.BOOK_TARGET || 'pdf';
  if (!TARGETS.includes(target)) {
    throw new Error(`BOOK_TARGET は ${TARGETS.join('・')} のどれかを指定してください（指定: ${target}）`);
  }
  return target;
}

const el = (tagName, properties, children) => ({ type: 'element', tagName, properties: properties || {}, children: children || [] });
const text = (value) => ({ type: 'text', value });

const classesOf = (node) => {
  const c = node.properties && node.properties.className;
  return Array.isArray(c) ? c : typeof c === 'string' ? c.split(/\s+/) : [];
};
const isElement = (node, tagName) => node && node.type === 'element' && (!tagName || node.tagName === tagName);
const hasClass = (node, name) => isElement(node) && classesOf(node).includes(name);

function walk(node, visit, parent = null) {
  if (visit(node, parent) === false) return;
  for (const child of [...(node.children || [])]) walk(child, visit, node);
}

/** h2に「5章 - 3」の見出しを入れる（data-chapter は vfm-section-label.js が付ける） */
function addSectionLabels(tree) {
  let n = 0;
  walk(tree, (node) => {
    if (!isElement(node, 'h2')) return;
    n += 1;
    const label = node.properties && node.properties.dataChapter;
    if (!label) return;
    node.children.unshift(el('span', { className: ['section-label'] }, [text(`${label} - ${n}`)]), text(' '));
  });
}

/** 脚注を本文の番号のリンクと文書の末尾の aside に分ける */
function splitFootnotes(tree) {
  const notes = [];
  walk(tree, (node) => {
    if (!node.children) return;
    node.children = node.children.map((child) => {
      if (!(isElement(child, 'span') && hasClass(child, 'footnote'))) return child;
      const n = notes.length + 1;
      notes.push({ n, children: child.children });
      return el('sup', { className: ['footnote-ref'] }, [
        el('a', { 'epub:type': 'noteref', href: `#fn-${n}`, id: `fnref-${n}`, role: 'doc-noteref' }, [text(String(n))]),
      ]);
    });
  });
  if (notes.length === 0) return;
  const body = tree.children.find((c) => isElement(c, 'html'))?.children.find((c) => isElement(c, 'body')) || tree;
  body.children.push(
    el('section', { className: ['footnotes'], 'epub:type': 'footnotes', role: 'doc-endnotes' }, [
      // h2にするとリーダーの目次に「注」が並ぶので、段落にする
      el('p', { className: ['footnotes-title'] }, [text('注')]),
      ...notes.map(({ n, children }) =>
        el('aside', { id: `fn-${n}`, className: ['footnote-body'], 'epub:type': 'footnote', role: 'doc-footnote' }, [
          el('p', {}, [el('a', { href: `#fnref-${n}`, className: ['footnote-back'] }, [text(String(n))]), text('　'), ...children]),
        ])),
    ]),
  );
}

/** コードの各行の頭に行番号を入れる */
function addCodeLineNumbers(tree) {
  walk(tree, (node) => {
    if (!(isElement(node, 'span') && hasClass(node, 'code-line'))) return;
    const line = node.properties && node.properties.dataLine;
    if (line === undefined || line === null || line === '') return;
    node.children.unshift(el('span', { className: ['code-line-number'] }, [text(String(line).padStart(3, ' ') + ' ')]));
    // 折り返した続きの行の字下げ（vfm-code-lines.js が書く padding-left と負の text-indent）に、行番号の4文字を足す
    const style = node.properties.style;
    const m = typeof style === 'string' && style.match(/padding-left:\s*(\d+)ch/);
    const indent = (m ? Number(m[1]) : 0) + 4;
    node.properties.style = `padding-left: ${indent}ch; text-indent: -${indent}ch;`;
    return false;
  });
}

/** 図をPNGに替え、幅を本文の幅に対する割合にする */
function useDiagramPngs(tree) {
  walk(tree, (node) => {
    if (!isElement(node, 'img')) return;
    const src = node.properties && node.properties.src;
    if (typeof src !== 'string' || !src.startsWith('images/diagrams/') || !src.endsWith('.svg')) return;
    const l = layoutForSrc(decodeURI(src));
    node.properties.src = `images/epub/diagrams/${path.basename(src, '.svg')}.png`;
    const percent = l ? Math.min(100, (l.widthMm / TEXT_WIDTH_MM) * 100) : 100;
    node.properties.style = `width: ${percent.toFixed(0)}%`;
  });
}

/**
 * 表のセルの align 属性（Markdownの表で揃えを指定したセルに付く）を text-align のスタイルに替える。
 * align はEPUB 3（HTML）では使えない属性で、epubcheckがエラーにする
 */
function replaceCellAlign(tree) {
  walk(tree, (node) => {
    if (!(isElement(node, 'th') || isElement(node, 'td'))) return;
    const align = node.properties && node.properties.align;
    if (!align) return;
    delete node.properties.align;
    const style = node.properties.style ? `${String(node.properties.style).replace(/;?\s*$/, '; ')}` : '';
    node.properties.style = `${style}text-align: ${align};`;
  });
}

function rehypeEpub() {
  return (tree) => {
    if (currentTarget() !== 'epub') return;
    addSectionLabels(tree);
    splitFootnotes(tree);
    addCodeLineNumbers(tree);
    useDiagramPngs(tree);
    replaceCellAlign(tree);
  };
}

module.exports = { rehypeEpub, currentTarget, TARGETS };
