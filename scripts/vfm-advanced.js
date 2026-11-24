/**
 * 初めて読むときは飛ばしてよい節の見出しに「発展」の印を付ける。
 *
 * 原稿では見出しの末尾に {.advanced} を書く（例: `### Mixinパターン {.advanced}`）。
 * VFMがその見出しに class="advanced" を付けるので、見出しの末尾に
 * <span class="advanced-label">発展</span> を足す。CSSの生成内容（::after）ではなく
 * HTMLに書き込むのは、生成内容を使えないEPUB（Kindle）でも同じ印を出すため。
 * 見た目はテーマ（theme.css・epub-theme.css）の .advanced-label で決める。
 *
 * ただしPDFのh2だけは、span を入れずテーマの h2.advanced::after で描く。h2の文字は柱
 * （string-set: section content()）に写るため、span だと柱にも「発展」が出てしまう。
 * content() は疑似要素を含まないので、::after なら柱に写らない。
 */
const { currentTarget } = require('./vfm-epub.js');
const HEADINGS = new Set(['h2', 'h3', 'h4', 'h5']);
const LABEL = '発展';

function walk(node, visit) {
  visit(node);
  for (const child of node.children || []) walk(child, visit);
}

function rehypeAdvanced() {
  return (tree) => {
    walk(tree, (node) => {
      if (node.type !== 'element' || !HEADINGS.has(node.tagName)) return;
      const cls = node.properties && node.properties.className;
      if (!Array.isArray(cls) || !cls.includes('advanced')) return;
      if (node.tagName === 'h2' && currentTarget() !== 'epub') return;
      node.children.push(
        { type: 'text', value: ' ' },
        {
          type: 'element',
          tagName: 'span',
          properties: { className: ['advanced-label'] },
          children: [{ type: 'text', value: LABEL }],
        },
      );
    });
  };
}

module.exports = { rehypeAdvanced, LABEL };
