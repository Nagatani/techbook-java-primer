#!/usr/bin/env python3
import re
import sys

def remove_unnecessary_bold(content, is_glossary=False):
    """Remove unnecessary bold formatting while preserving required patterns."""
    
    # Skip processing for glossary files
    if is_glossary:
        return content
    
    # Patterns to preserve (these will be temporarily replaced with placeholders)
    preserve_patterns = [
        (r'\*\*サンプルコード\d+-\d+\*\*', 'SAMPLE_CODE_'),
        (r'\*\*リスト\d+-\d+\*\*', 'LIST_'),
        (r'\*\*エラーメッセージ\*\*：', 'ERROR_MSG:'),
        (r'\*\*原因\*\*：', 'CAUSE:'),
        (r'\*\*対処法\*\*：', 'SOLUTION:'),
    ]
    
    # Create a working copy
    result = content
    
    # Temporarily replace patterns we want to preserve
    replacements = []
    for pattern, placeholder in preserve_patterns:
        matches = re.finditer(pattern, result)
        for i, match in enumerate(matches):
            temp_placeholder = f'__{placeholder}{i}__'
            replacements.append((temp_placeholder, match.group(0)))
            result = result.replace(match.group(0), temp_placeholder, 1)
    
    # Remove all remaining bold formatting
    result = re.sub(r'\*\*([^*]+)\*\*', r'\1', result)
    
    # Restore preserved patterns
    for placeholder, original in replacements:
        result = result.replace(placeholder, original)
    
    return result

def main():
    if len(sys.argv) != 2:
        print("Usage: python remove-bold.py <manuscript-file>")
        sys.exit(1)
    
    filename = sys.argv[1]
    
    try:
        with open(filename, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Check if this is a glossary file
        is_glossary = 'glossary' in filename.lower()
        
        modified_content = remove_unnecessary_bold(content, is_glossary)
        
        with open(filename, 'w', encoding='utf-8') as f:
            f.write(modified_content)
        
        print(f"Successfully processed {filename}")
        
    except Exception as e:
        print(f"Error processing {filename}: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()