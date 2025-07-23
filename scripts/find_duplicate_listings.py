#!/usr/bin/env python3
"""
Find and analyze duplicate code listing numbers in manuscript files.

This script searches for code listing numbers in various formats:
- **リストX.Y**
- **サンプルコードX-Y**
- **コードX.Y** or **コードX-Y**

It identifies duplicates, analyzes format consistency, and generates a detailed report.
"""

import os
import re
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Tuple, Set

class CodeListingAnalyzer:
    """Analyzer for code listing numbers in manuscript files."""
    
    def __init__(self, manuscripts_dir: str):
        self.manuscripts_dir = Path(manuscripts_dir)
        self.listings_by_chapter: Dict[str, List[Tuple[str, str, int, str]]] = defaultdict(list)
        self.all_listings: List[Tuple[str, str, str, int, str]] = []
        
        # Regular expressions for different listing formats
        self.patterns = [
            (r'\*\*リスト(\d+)\.(\d+)\*\*', 'リスト', '.'),
            (r'\*\*リスト(\d+)-(\d+)\*\*', 'リスト', '-'),
            (r'\*\*サンプルコード(\d+)-(\d+)\*\*', 'サンプルコード', '-'),
            (r'\*\*サンプルコード(\d+)\.(\d+)\*\*', 'サンプルコード', '.'),
            (r'\*\*コード(\d+)\.(\d+)\*\*', 'コード', '.'),
            (r'\*\*コード(\d+)-(\d+)\*\*', 'コード', '-'),
        ]
    
    def scan_manuscripts(self):
        """Scan all manuscript files for code listings."""
        for file_path in sorted(self.manuscripts_dir.glob("chapter*.md")):
            self._scan_file(file_path)
    
    def _scan_file(self, file_path: Path):
        """Scan a single file for code listings."""
        chapter_name = file_path.stem
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                lines = content.split('\n')
                
            for line_num, line in enumerate(lines, 1):
                for pattern, format_type, separator in self.patterns:
                    matches = re.finditer(pattern, line)
                    for match in matches:
                        chapter_num = match.group(1)
                        listing_num = match.group(2)
                        listing_id = f"{chapter_num}{separator}{listing_num}"
                        full_listing = f"{format_type}{listing_id}"
                        
                        # Store with all information
                        self.listings_by_chapter[chapter_name].append(
                            (listing_id, format_type, line_num, full_listing)
                        )
                        self.all_listings.append(
                            (chapter_name, listing_id, format_type, line_num, full_listing)
                        )
                        
        except Exception as e:
            print(f"Error reading {file_path}: {e}")
    
    def find_duplicates(self) -> Dict[str, List[Tuple[str, str, str, int, str]]]:
        """Find duplicate listing numbers across all chapters."""
        listings_map = defaultdict(list)
        
        for chapter, listing_id, format_type, line_num, full_listing in self.all_listings:
            key = f"{format_type}{listing_id}"
            listings_map[key].append((chapter, listing_id, format_type, line_num, full_listing))
        
        # Return only duplicates
        duplicates = {k: v for k, v in listings_map.items() if len(v) > 1}
        return duplicates
    
    def analyze_format_consistency(self) -> Dict[str, Set[str]]:
        """Analyze which chapters use mixed formats."""
        chapter_formats = defaultdict(set)
        
        for chapter, listings in self.listings_by_chapter.items():
            for _, format_type, _, _ in listings:
                chapter_formats[chapter].add(format_type)
        
        # Return chapters with mixed formats
        mixed_formats = {
            chapter: formats 
            for chapter, formats in chapter_formats.items() 
            if len(formats) > 1
        }
        return mixed_formats
    
    def analyze_separators(self) -> Dict[str, Dict[str, int]]:
        """Analyze separator usage (. vs -) by chapter and format."""
        separator_usage = defaultdict(lambda: defaultdict(int))
        
        for chapter, listing_id, format_type, _, _ in self.all_listings:
            if '.' in listing_id:
                separator = '.'
            else:
                separator = '-'
            separator_usage[chapter][f"{format_type}{separator}"] += 1
        
        return separator_usage
    
    def generate_report(self) -> str:
        """Generate a comprehensive report of the analysis."""
        report = []
        report.append("# Code Listing Analysis Report")
        report.append("=" * 80)
        report.append("")
        
        # Summary statistics
        total_listings = len(self.all_listings)
        chapters_with_listings = len(self.listings_by_chapter)
        report.append(f"## Summary Statistics")
        report.append(f"- Total code listings found: {total_listings}")
        report.append(f"- Chapters with listings: {chapters_with_listings}")
        report.append("")
        
        # Duplicates section
        duplicates = self.find_duplicates()
        report.append(f"## Duplicate Listings")
        report.append(f"Found {len(duplicates)} duplicate listing numbers:")
        report.append("")
        
        if duplicates:
            for listing_key, occurrences in sorted(duplicates.items()):
                report.append(f"### {listing_key}")
                for chapter, listing_id, format_type, line_num, full_listing in occurrences:
                    report.append(f"  - {chapter} (line {line_num}): **{full_listing}**")
                report.append("")
        else:
            report.append("No duplicates found!")
            report.append("")
        
        # Format consistency
        mixed_formats = self.analyze_format_consistency()
        report.append(f"## Chapters with Mixed Formats")
        report.append(f"Found {len(mixed_formats)} chapters using multiple formats:")
        report.append("")
        
        if mixed_formats:
            for chapter, formats in sorted(mixed_formats.items()):
                report.append(f"- {chapter}: {', '.join(sorted(formats))}")
            report.append("")
        else:
            report.append("All chapters use consistent formats!")
            report.append("")
        
        # Separator usage
        separator_usage = self.analyze_separators()
        report.append("## Separator Usage by Chapter")
        report.append("")
        
        for chapter in sorted(separator_usage.keys()):
            usages = separator_usage[chapter]
            report.append(f"### {chapter}")
            for usage_type, count in sorted(usages.items()):
                report.append(f"  - {usage_type}: {count}")
            report.append("")
        
        # Detailed listing by chapter
        report.append("## All Listings by Chapter")
        report.append("")
        
        for chapter in sorted(self.listings_by_chapter.keys()):
            listings = self.listings_by_chapter[chapter]
            if listings:
                report.append(f"### {chapter} ({len(listings)} listings)")
                
                # Sort by listing number
                sorted_listings = sorted(listings, key=lambda x: (int(x[0].split('.')[0].split('-')[0]), 
                                                                  int(x[0].split('.')[1] if '.' in x[0] else x[0].split('-')[1])))
                
                for listing_id, format_type, line_num, full_listing in sorted_listings:
                    report.append(f"  - Line {line_num:4d}: **{full_listing}**")
                report.append("")
        
        return "\n".join(report)


def main():
    """Main execution function."""
    # Project root directory
    project_root = Path(__file__).parent.parent
    manuscripts_dir = project_root / "manuscripts"
    
    if not manuscripts_dir.exists():
        print(f"Error: Manuscripts directory not found at {manuscripts_dir}")
        return
    
    print("Analyzing code listings in manuscript files...")
    print(f"Scanning directory: {manuscripts_dir}")
    print("")
    
    # Create analyzer and run analysis
    analyzer = CodeListingAnalyzer(manuscripts_dir)
    analyzer.scan_manuscripts()
    
    # Generate and print report
    report = analyzer.generate_report()
    print(report)
    
    # Save report to file
    report_path = project_root / "scripts" / "code_listing_analysis_report.txt"
    with open(report_path, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print("")
    print(f"Report saved to: {report_path}")


if __name__ == "__main__":
    main()