/**
 * リスト13-2
 * コードスニペット
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (93行目)
 */

// 匿名クラスを使ったボタンのクリック処理
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ボタンがクリックされました！");
    }
});