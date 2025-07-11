/**
 * リスト3-31
 * MembershipDiscountクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (1614行目)
 */

public class MembershipDiscount {
    public static void main(String[] args) {
        // 顧客情報の設定
        int age = 25;
        boolean isPremiumMember = true;
        int purchaseAmount = 5000;
        int membershipYears = 3;
        
        System.out.println("=== 顧客情報 ===");
        System.out.println("年齢: " + age + "歳");
        System.out.println("プレミアム会員: " + (isPremiumMember ? "はい" : "いいえ"));
        System.out.println("購入金額: " + purchaseAmount + "円");
        System.out.println("会員歴: " + membershipYears + "年");
        System.out.println();
        
        // 複数の割引条件を論理演算子で組み合わせた判定
        System.out.println("=== 割引判定結果 ===");
        
        // 条件1: シニア割引（65歳以上）
        if (age >= 65) {
            System.out.println("シニア割引が適用されます（20%オフ）");
        }
        
        // 条件2: プレミアム会員かつ高額購入
        if (isPremiumMember && purchaseAmount >= 3000) {
            System.out.println("プレミアム会員高額購入割引が適用されます（15%オフ）");
        }
        
        // 条件3: 若年層または長期会員
        if (age <= 25 || membershipYears >= 5) {
            System.out.println("若年層・長期会員割引が適用されます（10%オフ）");
        }
        
        // 条件4: 複雑な組み合わせ条件
        if ((isPremiumMember && purchaseAmount >= 5000) || 
            (age >= 60 && membershipYears >= 3)) {
            System.out.println("特別VIP割引が適用されます（25%オフ）");
        }
        
        // 条件5: 除外条件を含む複雑な判定
        if (purchaseAmount >= 10000 && age >= 20 && age <= 60 && !isPremiumMember) {
            System.out.println("一般会員高額購入割引が適用されます（5%オフ）");
        }
    }
}