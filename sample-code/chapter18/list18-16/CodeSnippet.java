/**
 * リスト18-16
 * コードスニペット
 * 
 * 元ファイル: chapter18-gui-basics.md (832行目)
 */

// 匿名クラスのバージョン
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ボタンがクリックされました！");
    }
});

// ラムダ式のバージョン
button.addActionListener(e -> {
    System.out.println("ボタンがクリックされました！");
});

// 処理が1行の場合はさらに簡潔に
button.addActionListener(e -> System.out.println("ボタンがクリックされました！"));