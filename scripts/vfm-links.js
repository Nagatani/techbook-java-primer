/**
 * PDFでリンクをクリックできるように、原稿のリンクを組み替える。
 *
 * 1. 本の中へのリンク（目次などの `chapter01-introduction.md#...`）の拡張子を .html に変える。
 *    原稿の .md はビルド時に .html になるため、.md のままだとVivliostyleが本の中の
 *    ページと結び付けられず、PDFでは開けない localhost のURLになってしまう。
 * 2. 本文中にインラインコードで書いたURL（`https://github.com/...`）をリンクにする。
 *    クリックするとブラウザで開く。URLは本文にそのまま見えているので、外部URLから
 *    脚注を作る処理（footnote-external-link.css）の対象から外す（class="code-url"）。
 *
 * コードブロック（pre）の中と、すでにリンクになっている箇所には手を付けない。
 */

const isElement = (node, tagName) => node && node.type === 'element' && (!tagName || node.tagName === tagName);

const URL_PATTERN = /^https?:\/\/[\x21-\x7e]+$/;
const LOCAL_MD = /^(?![a-z]+:)([^#?]+)\.md(#.*)?$/i;

const textOf = (node) => (node.type === 'text' ? node.value : (node.children || []).map(textOf).join(''));

function walk(node, visit, inSkip = false) {
  for (let i = 0; i < (node.children || []).length; i++) {
    const child = node.children[i];
    const replaced = visit(child, node, i, inSkip);
    if (replaced) continue;
    walk(child, visit, inSkip || isElement(child, 'pre') || isElement(child, 'a'));
  }
}

function rehypeLinks() {
  return (tree) => {
    walk(tree, (node, parent, index, inSkip) => {
      if (isElement(node, 'a')) {
        const href = node.properties && node.properties.href;
        const m = typeof href === 'string' && href.match(LOCAL_MD);
        if (m) node.properties.href = `${m[1]}.html${m[2] || ''}`;
        return false;
      }
      if (inSkip || !isElement(node, 'code')) return false;
      const url = textOf(node).trim();
      if (!URL_PATTERN.test(url)) return false;
      parent.children[index] = {
        type: 'element',
        tagName: 'a',
        properties: { href: url, className: ['code-url'] },
        children: [node],
      };
      return true;
    });
  };
}

module.exports = { rehypeLinks };
