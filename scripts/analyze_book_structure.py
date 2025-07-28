#!/usr/bin/env python3

import json
import re
import os
from datetime import datetime
from collections import defaultdict, Counter
import glob

class BookAnalyzer:
    def __init__(self, manuscripts_dir):
        self.manuscripts_dir = manuscripts_dir
        self.chapters = []
        self.appendices = []
        self.glossary_terms = []
        self.analysis = {
            "contentDuplication": [],
            "technicalTermFrequency": [],
            "chapterDependencies": [],
            "codeListingDistribution": defaultdict(int)
        }
        
    def analyze_all(self):
        """全体の分析を実行"""
        print("書籍構造の分析を開始します...")
        
        # 章ファイルの読み込み
        self._load_chapters()
        
        # 付録ファイルの読み込み
        self._load_appendices()
        
        # 用語集の読み込み
        self._load_glossary()
        
        # 全体分析
        self._analyze_content_duplication()
        self._analyze_technical_terms()
        self._analyze_dependencies()
        
        return self._create_json_structure()
    
    def _load_chapters(self):
        """章ファイルを読み込み分析"""
        chapter_files = sorted(glob.glob(os.path.join(self.manuscripts_dir, "chapter*.md")))
        
        for filepath in chapter_files:
            if os.path.basename(filepath) in ["chapter19a-event-basics.md", "chapter19b-event-types.md", "chapter19d-exercises.md"]:
                continue  # 分割された章のサブファイルはスキップ
                
            print(f"分析中: {os.path.basename(filepath)}")
            chapter_data = self._analyze_chapter_file(filepath)
            if chapter_data:
                self.chapters.append(chapter_data)
    
    def _load_appendices(self):
        """付録ファイルを読み込み分析"""
        appendix_files = sorted(glob.glob(os.path.join(self.manuscripts_dir, "appendix-*.md")))
        
        for filepath in appendix_files:
            if os.path.basename(filepath) == "appendix-index.md":
                continue  # インデックスファイルはスキップ
                
            print(f"分析中: {os.path.basename(filepath)}")
            appendix_data = self._analyze_appendix_file(filepath)
            if appendix_data:
                self.appendices.append(appendix_data)
    
    def _analyze_chapter_file(self, filepath):
        """個別の章ファイルを分析"""
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
            lines = content.split('\n')
        
        # 章番号の抽出
        filename = os.path.basename(filepath)
        chapter_match = re.match(r'chapter(\d+)', filename)
        if not chapter_match:
            return None
            
        chapter_num = int(chapter_match.group(1))
        
        # タイトルとサブタイトルの抽出
        title = ""
        subtitle = ""
        title_match = re.search(r'#\s*<b>(\d+)章</b>\s*<span>([^<]+)</span>\s*(?:<small>([^<]+)</small>)?', content)
        if title_match:
            title = title_match.group(2).strip()
            subtitle = title_match.group(3).strip() if title_match.group(3) else ""
        
        # 学習目標の抽出
        learning_objectives = self._extract_learning_objectives(content)
        
        # 前提知識の抽出
        prerequisites = self._extract_prerequisites(content)
        
        # 構造（見出し）の抽出
        structure = self._extract_structure(lines)
        
        # サンプルコードの抽出
        code_listings = self._extract_code_listings(lines)
        
        # キーワードの抽出
        keywords = self._extract_keywords(content)
        
        # 相互参照の抽出
        cross_references = self._extract_cross_references(content)
        
        # 統計情報
        stats = {
            "lineCount": len(lines),
            "characterCount": len(content),
            "codeListingCount": len(code_listings)
        }
        
        self.analysis["codeListingDistribution"][chapter_num] = len(code_listings)
        
        return {
            "number": chapter_num,
            "id": f"chapter{chapter_num:02d}",
            "title": title,
            "subtitle": subtitle,
            "filePath": os.path.basename(filepath),
            "stats": stats,
            "learningObjectives": learning_objectives,
            "prerequisites": prerequisites,
            "structure": structure,
            "codeListings": code_listings,
            "keywords": keywords,
            "crossReferences": cross_references
        }
    
    def _analyze_appendix_file(self, filepath):
        """個別の付録ファイルを分析"""
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
            lines = content.split('\n')
        
        # 付録IDの抽出
        filename = os.path.basename(filepath)
        letter_match = re.match(r'appendix-([a-h])', filename)
        if not letter_match:
            return None
            
        letter = letter_match.group(1).upper()
        
        # タイトルの抽出
        title = ""
        title_match = re.search(r'#\s*付録\s*[A-H][:：]\s*(.+)', content)
        if title_match:
            title = title_match.group(1).strip()
        
        # 統計情報
        stats = {
            "lineCount": len(lines),
            "characterCount": len(content)
        }
        
        return {
            "id": f"appendix-{letter_match.group(1)}",
            "letter": letter,
            "title": title,
            "filePath": os.path.basename(filepath),
            "stats": stats
        }
    
    def _extract_learning_objectives(self, content):
        """学習目標を抽出"""
        objectives = []
        
        # 「この章で学ぶこと」セクションを探す
        match = re.search(r'###?\s*この章で学ぶこと\s*\n([\s\S]*?)(?=\n###?|$)', content)
        if match:
            section_content = match.group(1)
            # 番号付きリストを抽出
            list_items = re.findall(r'\d+\.\s*(.+?)(?=\n\s*[\+\-\*]|\n\d+\.|\n\n|$)', section_content, re.DOTALL)
            objectives.extend([item.strip() for item in list_items])
        
        return objectives
    
    def _extract_prerequisites(self, content):
        """前提知識を抽出"""
        prerequisites = []
        
        # 「この章を始める前に」セクションを探す
        match = re.search(r'###?\s*この章を始める前に\s*\n([\s\S]*?)(?=\n###?|$)', content)
        if match:
            section_content = match.group(1)
            # 文章から前提条件を抽出（簡易的な実装）
            if "C言語" in section_content:
                prerequisites.append("C言語の基礎知識")
            if "オブジェクト指向" in section_content:
                prerequisites.append("オブジェクト指向の基本概念")
            # 他の章への参照
            chapter_refs = re.findall(r'第(\d+)章', section_content)
            for ref in chapter_refs:
                prerequisites.append(f"第{ref}章の内容")
        
        return prerequisites
    
    def _extract_structure(self, lines):
        """見出し構造を抽出"""
        sections = []
        current_section = None
        
        for i, line in enumerate(lines):
            # 見出しの検出
            heading_match = re.match(r'^(#{2,6})\s+(.+)', line)
            if heading_match:
                level = len(heading_match.group(1))
                title = re.sub(r'<[^>]+>', '', heading_match.group(2)).strip()
                
                if level == 2:
                    if current_section:
                        sections.append(current_section)
                    current_section = {
                        "level": level,
                        "title": title,
                        "lineNumber": i + 1,
                        "subsections": []
                    }
                elif level >= 3 and current_section:
                    current_section["subsections"].append({
                        "level": level,
                        "title": title,
                        "lineNumber": i + 1
                    })
        
        if current_section:
            sections.append(current_section)
        
        return {"sections": sections}
    
    def _extract_code_listings(self, lines):
        """サンプルコードを抽出"""
        code_listings = []
        
        for i, line in enumerate(lines):
            # サンプルコード番号の検出
            code_match = re.search(r'サンプルコード(\d+)-(\d+)', line)
            if code_match:
                # 次の行でコードブロックの言語を確認
                language = "unknown"
                if i + 1 < len(lines):
                    lang_match = re.match(r'^```(\w+)', lines[i + 1])
                    if lang_match:
                        language = lang_match.group(1)
                
                code_listings.append({
                    "number": f"サンプルコード{code_match.group(1)}-{code_match.group(2)}",
                    "lineNumber": i + 1,
                    "language": language
                })
        
        return code_listings
    
    def _extract_keywords(self, content):
        """重要なキーワードを抽出"""
        keywords = []
        
        # 技術用語のパターン
        tech_terms = re.findall(r'\*\*([^*]+)\*\*', content)
        keywords.extend(tech_terms)
        
        # Javaキーワード
        java_keywords = ['class', 'interface', 'extends', 'implements', 'public', 'private', 
                        'protected', 'static', 'final', 'abstract', 'synchronized']
        for keyword in java_keywords:
            if re.search(r'\b' + keyword + r'\b', content, re.IGNORECASE):
                keywords.append(keyword)
        
        # 重複を除去
        return list(set(keywords))[:20]  # 上位20個まで
    
    def _extract_cross_references(self, content):
        """相互参照を抽出"""
        chapters_referenced = []
        appendices_referenced = []
        
        # 章への参照
        chapter_refs = re.findall(r'第(\d+)章', content)
        chapters_referenced = list(set([int(ref) for ref in chapter_refs]))
        
        # 付録への参照
        appendix_refs = re.findall(r'付録\s*([A-H])', content)
        appendices_referenced = list(set(appendix_refs))
        
        return {
            "chaptersReferenced": sorted(chapters_referenced),
            "appendicesReferenced": sorted(appendices_referenced)
        }
    
    def _load_glossary(self):
        """用語集を読み込み"""
        glossary_path = os.path.join(self.manuscripts_dir, "glossary.md")
        if not os.path.exists(glossary_path):
            return
        
        with open(glossary_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 用語の抽出
        term_pattern = re.compile(r'^-\s*\*\*([^*]+)\*\*\s*-\s*(.+)$', re.MULTILINE)
        matches = term_pattern.findall(content)
        
        current_category = ""
        for match in matches:
            term = match[0]
            references = match[1]
            
            # カテゴリの検出
            cat_match = re.search(r'^###\s*([A-Z])\s*$', content, re.MULTILINE)
            if cat_match:
                current_category = cat_match.group(1)
            
            # 章番号の抽出
            chapters = []
            chapter_refs = re.findall(r'第(\d+)章', references)
            chapters = [int(ref) for ref in chapter_refs]
            
            self.glossary_terms.append({
                "term": term,
                "chapters": chapters,
                "category": current_category
            })
    
    def _analyze_content_duplication(self):
        """内容の重複をチェック"""
        # 簡易的な実装：各章で扱われるトピックを比較
        topic_chapters = defaultdict(list)
        
        common_topics = {
            "クラス": ["class", "クラス", "Class"],
            "継承": ["extends", "継承", "inheritance"],
            "インターフェイス": ["interface", "インターフェイス"],
            "例外処理": ["exception", "例外", "try", "catch"],
            "コレクション": ["Collection", "List", "ArrayList", "コレクション"],
            "ジェネリクス": ["Generic", "ジェネリクス", "<T>"],
            "ラムダ式": ["lambda", "ラムダ", "->"],
            "Stream API": ["Stream", "stream()", "filter", "map"]
        }
        
        for chapter in self.chapters:
            filepath = os.path.join(self.manuscripts_dir, chapter["filePath"])
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read().lower()
            
            for topic, keywords in common_topics.items():
                if any(keyword.lower() in content for keyword in keywords):
                    topic_chapters[topic].append(chapter["number"])
        
        # 重複の分析
        for topic, chapters in topic_chapters.items():
            if len(chapters) > 2:
                self.analysis["contentDuplication"].append({
                    "topic": topic,
                    "chapters": sorted(chapters),
                    "severity": "high" if len(chapters) > 4 else "medium"
                })
    
    def _analyze_technical_terms(self):
        """技術用語の頻度を分析"""
        term_counter = Counter()
        term_chapters = defaultdict(set)
        
        # 重要な技術用語のリスト
        important_terms = [
            "クラス", "オブジェクト", "インスタンス", "メソッド", "フィールド",
            "継承", "ポリモーフィズム", "カプセル化", "抽象化", "インターフェイス",
            "例外", "コレクション", "ジェネリクス", "ラムダ式", "Stream",
            "スレッド", "同期", "ネットワーク", "GUI", "イベント"
        ]
        
        for chapter in self.chapters:
            filepath = os.path.join(self.manuscripts_dir, chapter["filePath"])
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            for term in important_terms:
                count = content.count(term)
                if count > 0:
                    term_counter[term] += count
                    term_chapters[term].add(chapter["number"])
        
        # 頻度順にソート
        for term, frequency in term_counter.most_common(15):
            self.analysis["technicalTermFrequency"].append({
                "term": term,
                "frequency": frequency,
                "chapters": sorted(list(term_chapters[term]))
            })
    
    def _analyze_dependencies(self):
        """章間の依存関係を分析"""
        # 基本的な依存関係のルール
        dependencies = [
            {"chapter": 3, "dependsOn": [2], "reason": "クラスの基本概念が必要"},
            {"chapter": 4, "dependsOn": [3], "reason": "オブジェクト指向の基礎が必要"},
            {"chapter": 5, "dependsOn": [3, 4], "reason": "クラスとインスタンスの理解が必要"},
            {"chapter": 6, "dependsOn": [3, 4], "reason": "オブジェクトの基本が必要"},
            {"chapter": 7, "dependsOn": [5], "reason": "継承の概念が必要"},
            {"chapter": 11, "dependsOn": [10], "reason": "コレクションの基礎が必要"},
            {"chapter": 12, "dependsOn": [7], "reason": "インターフェイスの理解が必要"},
            {"chapter": 13, "dependsOn": [10, 11, 12], "reason": "コレクションとジェネリクス、ラムダ式が必要"},
            {"chapter": 16, "dependsOn": [3, 4], "reason": "基本的なJavaプログラミングが必要"},
            {"chapter": 18, "dependsOn": [3, 4, 7], "reason": "クラスとイベント処理の基礎が必要"},
            {"chapter": 19, "dependsOn": [18], "reason": "GUI基礎の理解が必要"},
            {"chapter": 20, "dependsOn": [18, 19], "reason": "基本的なGUIプログラミングが必要"},
            {"chapter": 21, "dependsOn": [3, 4], "reason": "クラスとメソッドの理解が必要"}
        ]
        
        self.analysis["chapterDependencies"] = dependencies
    
    def _create_json_structure(self):
        """最終的なJSON構造を作成"""
        total_code_listings = sum(self.analysis["codeListingDistribution"].values())
        
        return {
            "meta": {
                "totalChapters": len(self.chapters),
                "totalAppendices": len(self.appendices),
                "generatedAt": datetime.now().isoformat(),
                "bookTitle": "Java入門"
            },
            "chapters": sorted(self.chapters, key=lambda x: x["number"]),
            "appendices": sorted(self.appendices, key=lambda x: x["letter"]),
            "glossary": {
                "terms": self.glossary_terms
            },
            "analysis": {
                "contentDuplication": self.analysis["contentDuplication"],
                "technicalTermFrequency": self.analysis["technicalTermFrequency"],
                "chapterDependencies": self.analysis["chapterDependencies"],
                "codeListingDistribution": {
                    "byChapter": dict(self.analysis["codeListingDistribution"]),
                    "total": total_code_listings
                }
            },
            "improvements": {
                "suggestions": self._generate_improvement_suggestions()
            }
        }
    
    def _generate_improvement_suggestions(self):
        """改善提案を生成"""
        suggestions = []
        
        # 重複コンテンツの削減提案
        for dup in self.analysis["contentDuplication"]:
            if dup["severity"] == "high":
                suggestions.append({
                    "type": "重複削減",
                    "description": f"{dup['topic']}に関する内容が{len(dup['chapters'])}章で重複しています。内容を整理し、相互参照で対応することを推奨します。",
                    "chapters": dup["chapters"]
                })
        
        # コードリスティングの偏り
        code_dist = self.analysis["codeListingDistribution"]
        avg_listings = sum(code_dist.values()) / len(code_dist) if code_dist else 0
        for chapter, count in code_dist.items():
            if count > avg_listings * 2:
                suggestions.append({
                    "type": "コード例の調整",
                    "description": f"第{chapter}章のサンプルコードが多すぎる可能性があります（{count}個）。一部を付録に移動することを検討してください。",
                    "chapters": [chapter]
                })
        
        # 依存関係の複雑さ
        complex_deps = [d for d in self.analysis["chapterDependencies"] if len(d["dependsOn"]) > 2]
        if complex_deps:
            for dep in complex_deps:
                suggestions.append({
                    "type": "依存関係の簡素化",
                    "description": f"第{dep['chapter']}章は{len(dep['dependsOn'])}章に依存しています。学習順序を再検討してください。",
                    "chapters": [dep["chapter"]] + dep["dependsOn"]
                })
        
        return suggestions


def main():
    manuscripts_dir = "/Users/nagatani/github/techbook-java-primer/manuscripts"
    output_dir = "/Users/nagatani/github/techbook-java-primer/docs/analysis"
    
    # 出力ディレクトリの作成
    os.makedirs(output_dir, exist_ok=True)
    
    # 分析の実行
    analyzer = BookAnalyzer(manuscripts_dir)
    result = analyzer.analyze_all()
    
    # JSONファイルの出力
    output_path = os.path.join(output_dir, "book-structure.json")
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)
    
    print(f"\n分析完了！結果を保存しました: {output_path}")
    
    # 簡易レポートの表示
    print("\n=== 分析サマリー ===")
    print(f"総章数: {result['meta']['totalChapters']}")
    print(f"総付録数: {result['meta']['totalAppendices']}")
    print(f"総サンプルコード数: {result['analysis']['codeListingDistribution']['total']}")
    print(f"重複トピック数: {len(result['analysis']['contentDuplication'])}")
    print(f"改善提案数: {len(result['improvements']['suggestions'])}")


if __name__ == "__main__":
    main()