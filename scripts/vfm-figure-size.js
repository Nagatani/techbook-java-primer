/**
 * 図（images/diagrams/ の画像）の <img> に、紙面に載せる幅を書き込む。
 *
 * 幅の決め方は scripts/diagram-size.js にまとめてある。図の元の大きさに一定の倍率を
 * 掛け、本文の幅とページに収まる高さを上限にする。テーマの .figure img は大きさを
 * 決めず、ここで書き込んだ幅と高さを使う。
 */
const { layoutForSrc } = require('./diagram-size.js');

function walk(node, visit) {
  visit(node);
  for (const child of node.children || []) walk(child, visit);
}

function rehypeFigureSize() {
  return (tree) => {
    walk(tree, (node) => {
      if (node.type !== 'element' || node.tagName !== 'img') return;
      const src = node.properties && node.properties.src;
      if (typeof src !== 'string' || !src.startsWith('images/diagrams/')) return;
      const l = layoutForSrc(decodeURI(src));
      if (!l) return;
      // 高さも書く。幅だけだと高さの計算がずれ、object-fit: contain で画像が小さく描かれる
      node.properties.style = `width: ${l.widthMm.toFixed(1)}mm; height: ${l.heightMm.toFixed(1)}mm`;
    });
  };
}

module.exports = { rehypeFigureSize };
