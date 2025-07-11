/**
 * リスト2-9
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (446行目)
 */

// for文
for (int i = 0; i < 10; i++) {
    System.out.println("i = " + i);
}

// 拡張for文（foreach）
int[] numbers = {1, 2, 3, 4, 5};
for (int num : numbers) {
    System.out.println(num);
}

// while文
int count = 0;
while (count < 5) {
    System.out.println("count = " + count);
    count++;
}