/**
 * 章・付録の中のセクション見出し（h2）に、章番号を data-chapter 属性として付ける。
 *
 * Vivliostyleでは原稿ファイルごとに別のHTML文書になるため、CSSのカウンターでは
 * 章番号を文書をまたいで受け渡せない。そこでビルド時に章タイトル（h1）の
 * <b>5章</b>・<b>付録A</b> から番号を読み取り、h2に書き込む。
 * 表示はテーマ側で attr(data-chapter) とセクションのカウンターを組み合わせて行う
 * （例: 「5章 - 3」）。章番号のない文書（目次・用語集など）のh2には何も付けない。
 *
 * あわせて、コードブロックを1行ずつに分ける処理（vfm-code-lines.js）と、
 * 索引の項目を用語と参照先に分ける処理（vfm-index-entries.js）も組み込む。
 */
const { VFM } = require('@vivliostyle/vfm');
const { rehypeCodeLines } = require('./vfm-code-lines.js');
const { rehypeIndexEntries } = require('./vfm-index-entries.js');

const LABEL_PATTERN = /^(\d+章|付録[A-Z])$/;

function textOf(node) {
  if (node.type === 'text') return node.value;
  return (node.children || []).map(textOf).join('');
}

function findAll(node, predicate, found = []) {
  if (predicate(node)) found.push(node);
  for (const child of node.children || []) findAll(child, predicate, found);
  return found;
}

const isElement = (tagName) => (node) => node.type === 'element' && node.tagName === tagName;

function rehypeSectionLabel() {
  return (tree) => {
    const [h1] = findAll(tree, isElement('h1'));
    if (!h1) return;
    const b = (h1.children || []).find(isElement('b'));
    const label = b && textOf(b).trim();
    if (!label || !LABEL_PATTERN.test(label)) return;
    for (const h2 of findAll(tree, isElement('h2'))) {
      h2.properties = { ...h2.properties, dataChapter: label };
    }
  };
}

/** vivliostyle.config.js の documentProcessor に渡す */
function documentProcessor(options, metadata) {
  // VFMの処理系は凍結されているため、呼び出して複製してからプラグインを足す
  return VFM(options, metadata)().use(rehypeSectionLabel).use(rehypeCodeLines).use(rehypeIndexEntries);
}

module.exports = { documentProcessor, rehypeSectionLabel };
