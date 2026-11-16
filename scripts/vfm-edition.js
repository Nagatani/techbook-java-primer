/**
 * 版（エディション）ごとに原稿の一部を出し分ける。
 *
 * 版は環境変数 BOOK_EDITION で選ぶ（vivliostyle.config.js が読む）。
 *   alpha   Webで配布する無料のALPHA版（既定）
 *   release 電子版・オンデマンド印刷で出版する正規版
 *
 * 原稿では、片方の版だけに出す要素に class="edition-alpha" か class="edition-release" を付ける
 * （表紙の「ALPHA VERSION」の表示、奥付の発行日など）。選ばれていない版の要素はHTMLから取り除く。
 * あわせて、<span data-build-date></span> をビルドした日付（2026年11月16日）に、
 * <span data-build-year></span> をビルドした年に置き換える。
 */

const EDITIONS = ['alpha', 'release'];

function currentEdition() {
  const edition = process.env.BOOK_EDITION || 'alpha';
  if (!EDITIONS.includes(edition)) {
    throw new Error(`BOOK_EDITION は ${EDITIONS.join(' か ')} を指定してください（指定: ${edition}）`);
  }
  return edition;
}

const classesOf = (node) => {
  const c = node.properties && node.properties.className;
  return Array.isArray(c) ? c : typeof c === 'string' ? c.split(/\s+/) : [];
};

function rehypeEdition() {
  const edition = currentEdition();
  const now = new Date();
  const date = `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`;
  const year = String(now.getFullYear());
  const walk = (node) => {
    if (!node.children) return;
    node.children = node.children.filter((child) => {
      if (child.type !== 'element') return true;
      const other = classesOf(child).find((c) => c.startsWith('edition-') && c !== `edition-${edition}`);
      return !other;
    });
    for (const child of node.children) {
      if (child.type !== 'element') continue;
      const p = child.properties || {};
      if ('dataBuildDate' in p) child.children = [{ type: 'text', value: date }];
      else if ('dataBuildYear' in p) child.children = [{ type: 'text', value: year }];
      else walk(child);
    }
  };
  return (tree) => walk(tree);
}

module.exports = { rehypeEdition, currentEdition, EDITIONS };
