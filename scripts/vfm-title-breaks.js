/**
 * 章・付録の題名（h1 の span と small）に、文節の切れ目だけで折り返す位置（<wbr>）を入れる。
 *
 * 題名は大きな文字で帯の中に置くため、「Java入門と開発環境構 / 築」のように1文字だけが
 * 次の行に送られることがある。テーマでは h1 span・h1 small を word-break: keep-all（<wbr>と空白でだけ折り返す）と
 * text-wrap: balance（行の長さをそろえる）にし、ここで次の位置に折り返し位置を入れる。
 *
 * - ひらがなの後ろで、次が漢字・カタカナ・英字・「（」のもの（「Java入門と / 開発」「特殊な / クラス」「作る / デスクトップ」）。
 *   ただし前後を漢字などに挟まれた1文字のひらがなは、助詞のときだけ（「考え方」「振り返る」は切らない）
 * - 「的」の後ろ（次が漢字のもの。「理論的 / 基盤」）
 * - 「・」「、」「）」の後ろと「（」の前
 * - 漢字・カタカナ・英字のうち、種類の違う文字の境目（「外部 / ライブラリ」「GUI / プログラミング」）。
 *   カタカナの後ろに漢字が続く位置は除く（「ソフトウェア工学」）
 * - カタカナが続く箇所で、下の語の前（「コレクション / フレームワーク」）
 *
 * Intl.Segmenter の単語の区切りは「ビル / ド」「マルチ / ス / レッド」のように誤りが多いため使わない。
 * 切れ目のない長い並び（題名は10文字、副題は20文字を超えるもの）は、はみ出さないよう半分の位置にも
 * 折り返し位置を入れる（いまの題名・副題には該当するものがない。新しい語で働いたら KATAKANA_WORDS に語を足す）。
 */

// カタカナが続く箇所で、前で折り返してよい語
const KATAKANA_WORDS = [
  'プログラミング', 'フレームワーク', 'パターン', 'インターフェイス', 'ライブラリ', 'キーワード',
  'プロジェクト', 'デプロイメント', 'デプロイ', 'ポリモーフィズム', 'インスタンス', 'イベント',
  'コレクション', 'クラス', 'テスト', 'エラー', 'オブジェクト', 'アプリケーション', 'システム',
];
const MAX_RUN = { span: 10, small: 20 };

const KANJI = /[一-鿿々〆]/;
const KATAKANA = /[ァ-ヺー]/;
const HIRAGANA = /[ぁ-ゖ]/;
const PARTICLES = /[とのなやをにでがはへもか]/;
const ALNUM = /[A-Za-z0-9]/;
const kind = (c) => (KANJI.test(c) ? 'kanji' : KATAKANA.test(c) ? 'katakana' : ALNUM.test(c) ? 'alnum' : null);

const isElement = (node, tagName) => node && node.type === 'element' && node.tagName === tagName;

/** 文字列のうち、折り返してよい位置（その文字の前）の添字の集合。maxRun を超える切れ目のない並びは半分でも切る */
function breakPoints(text, maxRun = MAX_RUN.span) {
  const chars = [...text];
  const points = new Set();
  for (let i = 1; i < chars.length; i++) {
    const prev = chars[i - 1];
    const next = chars[i];
    const k1 = kind(prev);
    const k2 = kind(next);
    // 漢字などに挟まれた1文字のひらがなは、助詞のときだけ切る（「考え方」「振り返る」の送り仮名では切らない）
    const lone = HIRAGANA.test(prev) && !(i >= 2 && HIRAGANA.test(chars[i - 2]));
    if (HIRAGANA.test(prev) && (k2 || next === '（') && (!lone || PARTICLES.test(prev))) points.add(i);
    else if (prev === '的' && KANJI.test(next)) points.add(i);
    else if (/[・、）]/.test(prev) || next === '（') points.add(i);
    // カタカナの後ろに漢字が続く位置は、ひと続きの語のことが多いので切らない（「ソフトウェア工学」「デプロイメント技術」）
    else if (k1 && k2 && k1 !== k2 && !(k1 === 'katakana' && k2 === 'kanji')) points.add(i);
    else if (k1 === 'katakana' && k2 === 'katakana' && KATAKANA_WORDS.some((w) => chars.slice(i, i + w.length).join('') === w)) points.add(i);
  }
  // 空白でも折り返すので、空白と折り返し位置で区切った並びごとに長さを確かめる
  let start = 0;
  for (let i = 0; i <= chars.length; i++) {
    const space = i < chars.length && /\s/.test(chars[i]);
    if (i === chars.length || space || points.has(i)) {
      const len = i - start;
      if (len > maxRun) points.add(start + Math.ceil(len / 2));
      start = space ? i + 1 : i;
    }
  }
  return [...points].filter((i) => i > 0 && i < chars.length && !/\s/.test(chars[i]) && !/\s/.test(chars[i - 1])).sort((a, b) => a - b);
}

/** テキストノードを、折り返し位置に <wbr> をはさんだノードの並びにする */
/**
 * テキストノードを、折り返し位置に <wbr> をはさんだノードの並びにする。
 * 「&」の前後はブラウザーが折り返すことがあるので、「&」を含む区切りは折り返さない span で囲む（「ドラッグ&ドロップ」）
 */
function splitText(value, maxRun) {
  const chars = [...value];
  const points = breakPoints(value, maxRun);
  const out = [];
  let from = 0;
  for (const p of [...points, chars.length]) {
    const part = chars.slice(from, p).join('');
    out.push(part.includes('&')
      ? { type: 'element', tagName: 'span', properties: { style: 'white-space: nowrap;' }, children: [{ type: 'text', value: part }] }
      : { type: 'text', value: part });
    if (p < chars.length) out.push({ type: 'element', tagName: 'wbr', properties: {}, children: [] });
    from = p;
  }
  return out;
}

function rehypeTitleBreaks() {
  return (tree) => {
    const visit = (node) => {
      if (isElement(node, 'h1')) {
        for (const child of node.children || []) {
          if (!isElement(child, 'span') && !isElement(child, 'small')) continue;
          child.children = (child.children || []).flatMap((c) => (c.type === 'text' ? splitText(c.value, MAX_RUN[child.tagName]) : [c]));
        }
        return;
      }
      (node.children || []).forEach(visit);
    };
    visit(tree);
  };
}

module.exports = { rehypeTitleBreaks, breakPoints };
