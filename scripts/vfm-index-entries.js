/**
 * 索引・用語集（manuscripts/glossary.md）の項目を、組版しやすい形に組み替える。
 *
 * 原稿は「- **用語** - 第9章、付録B.06」の形で書く（scripts/check-structure.js がこの形を読む）。
 * ビルド時に、用語を span.index-term、参照先を span.index-refs に分け、間の「 - 」を取り除く。
 * 見た目（用語と参照先の間隔、折り返しのぶら下げなど）はテーマ（glossary-theme.css）で決める。
 * .index-page を含まない文書には何もしない。
 */

const isElement = (node, tagName) => node && node.type === 'element' && (!tagName || node.tagName === tagName);
const hasClass = (node, name) => {
  const c = node.properties && node.properties.className;
  return Array.isArray(c) ? c.includes(name) : c === name;
};

function walk(node, visit) {
  visit(node);
  for (const child of node.children || []) walk(child, visit);
}

function rehypeIndexEntries() {
  return (tree) => {
    let isIndex = false;
    walk(tree, (node) => { if (isElement(node, 'div') && hasClass(node, 'index-page')) isIndex = true; });
    if (!isIndex) return;
    walk(tree, (li) => {
      if (!isElement(li, 'li')) return;
      const kids = (li.children || []).filter((n) => !(n.type === 'text' && !n.value.trim()));
      const [term, sep] = kids;
      if (!isElement(term, 'strong') || !sep || sep.type !== 'text' || !/^\s*-\s/.test(sep.value)) return;
      const rest = kids.slice(1);
      rest[0] = { type: 'text', value: sep.value.replace(/^\s*-\s+/, '') };
      li.children = [
        { type: 'element', tagName: 'span', properties: { className: ['index-term'] }, children: term.children },
        { type: 'element', tagName: 'span', properties: { className: ['index-refs'] }, children: rest },
      ];
    });
  };
}

module.exports = { rehypeIndexEntries };
