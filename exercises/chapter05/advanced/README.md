# 第5章 応用課題

## 🎯 学習目標
- 継承階層の適切な設計
- ポリモーフィズムの実践的活用
- メソッドオーバーライドの効果的な使用
- 抽象的な概念の具象化
- オブジェクト指向設計パターンの理解

## 📝 課題一覧

### 課題1: 動物園管理システム
**ファイル名**: `ZooManagement.java`

様々な動物を管理する動物園システムを作成してください。

**要求仕様**:
- 動物の基底クラスと具象クラス
- 種別に応じた特別な行動
- 飼育管理と健康チェック
- 来園者向けの展示機能

**継承階層**:
```
Animal (基底クラス)
├── Mammal (哺乳類)
│   ├── Lion (ライオン)
│   ├── Elephant (象)
│   └── Monkey (猿)
├── Bird (鳥類)
│   ├── Eagle (鷲)
│   └── Penguin (ペンギン)
└── Reptile (爬虫類)
    ├── Snake (蛇)
    └── Turtle (亀)
```

**実行例**:
```
=== 動物園管理システム ===

🦁 サファリパーク動物園

=== 動物登録 ===
✓ ライオン (レオ) - オス, 5歳
  特徴: 百獣の王、群れのリーダー
  餌: 肉食 (牛肉, 15kg/日)

✓ 象 (ジャンボ) - メス, 12歳
  特徴: 知能が高く記憶力抜群
  餌: 草食 (干し草, 200kg/日)

✓ ペンギン (ピコ) - オス, 3歳
  特徴: 水中を飛ぶように泳ぐ
  餌: 魚類 (魚, 3kg/日)

=== 動物の行動 ===
レオ (ライオン):
🦁 ガオー！威嚇の鳴き声
🏃 草原を時速50kmで駆ける
😴 20時間の休息タイム

ジャンボ (象):
🐘 パオーン！挨拶の鳴き声  
🚿 鼻で水浴びして体温調節
🤝 群れの仲間と社会的交流

ピコ (ペンギン):
🐧 クワッ！仲間を呼ぶ声
🏊 時速25kmで優雅に水中遊泳
🕺 求愛ダンスでパートナーにアピール

=== 飼育管理 ===
本日の給餌スケジュール:
- 08:00 レオ (ライオン): 牛肉 15kg
- 09:00 ジャンボ (象): 干し草 200kg  
- 10:00 ピコ (ペンギン): 魚 3kg

健康チェック結果:
✓ レオ: 体重65kg, 体温38.5°C - 健康
✓ ジャンボ: 体重4200kg, 体温36.8°C - 健康
⚠️ ピコ: 体重12kg, 体温40.2°C - 軽度発熱

=== 来園者向け展示 ===
現在公開中の動物:
🦁 サバンナエリア: ライオン (レオ)
   「百獣の王の迫力ある雄叫びをお楽しみください」
   
🐘 アフリカエリア: 象 (ジャンボ)  
   「賢い象の芸当と水浴びタイムは必見です」
   
🐧 南極エリア: ペンギン (ピコ)
   「愛らしいペンギンの泳ぎとダンスをご覧ください」

=== 動物園統計 ===
総動物数: 3匹
哺乳類: 2匹, 鳥類: 1匹, 爬虫類: 0匹
平均年齢: 6.7歳
1日の総餌代: ¥18,500
```

**評価ポイント**:
- 適切な継承階層の設計
- ポリモーフィズムの活用
- 動物種別の特性表現
- 管理機能の実装

---

### 課題2: 交通手段シミュレーター
**ファイル名**: `TransportSimulator.java`

様々な交通手段の動作をシミュレートするシステムを作成してください。

**要求仕様**:
- 交通手段の基底クラス
- 移動方法の違いによる分類
- 燃料・エネルギー管理
- 交通状況のシミュレーション

**継承階層**:
```
Vehicle (基底クラス)
├── LandVehicle (陸上車両)
│   ├── Car (自動車)
│   ├── Bicycle (自転車)
│   └── Train (電車)
├── AirVehicle (航空機)
│   ├── Airplane (飛行機)
│   └── Helicopter (ヘリコプター)
└── WaterVehicle (水上車両)
    ├── Ship (船)
    └── Submarine (潜水艦)
```

**実行例**:
```
=== 交通手段シミュレーター ===

🚗 トランスポート・シミュレーター v2.0

=== 車両初期化 ===
✓ 自動車 (Toyota Prius) 登録
  燃料: ハイブリッド, 燃費: 30km/L
  最高速度: 180km/h

✓ 飛行機 (Boeing 747) 登録  
  燃料: ジェット燃料, 燃費: 3km/L
  最高速度: 900km/h, 最大高度: 13,000m

✓ 船 (Cruise Queen) 登録
  燃料: 重油, 燃費: 1km/L  
  最高速度: 45km/h, 最大積載量: 3,000人

=== 移動シミュレーション ===
目的地: 東京 → 大阪 (距離: 500km)

Toyota Prius (自動車):
🚗 高速道路を使用してルート計算
⛽ 燃料消費: 16.7L (¥2,500)
⏱️ 所要時間: 5時間30分
🛣️ 経路: 東名高速 → 名神高速
✅ 到着完了

Boeing 747 (飛行機):
✈️ 飛行高度13,000mで巡航
⛽ 燃料消費: 166.7L (¥33,340)  
⏱️ 所要時間: 1時間15分
🛫 経路: 羽田空港 → 関西国際空港
✅ 到着完了

Cruise Queen (船):
🚢 瀬戸内海航路を選択
⛽ 燃料消費: 500L (¥45,000)
⏱️ 所要時間: 18時間30分  
🌊 経路: 東京湾 → 瀬戸内海 → 大阪湾
✅ 到着完了

=== 環境影響評価 ===
CO2排出量比較:
🚗 自動車: 39.2kg CO2
✈️ 飛行機: 392.5kg CO2  
🚢 船: 1,225kg CO2

効率ランキング:
1位: 自動車 (時間効率: ★★★, 環境効率: ★★★★)
2位: 飛行機 (時間効率: ★★★★★, 環境効率: ★★)
3位: 船 (時間効率: ★, 環境効率: ★)

=== 特殊動作デモ ===
Toyota Prius:
🚗 エンジン始動 → 電気モード → ハイブリッドモード
🔋 回生ブレーキで充電中
⚠️ 燃料残量警告: 残り50km

Boeing 747:
✈️ 離陸準備 → 滑走 → 上昇 → 巡航
📡 航空管制との交信中
☁️ 気象レーダーで乱気流回避

Cruise Queen:
🚢 出港準備 → 係留解除 → 航行
⚓ 錨を上げて出港
📍 GPS航法システム作動中
```

**評価ポイント**:
- 交通手段の特性の抽象化
- 移動アルゴリズムの実装
- エネルギー管理システム
- リアルな動作シミュレーション

---

### 課題3: ゲームキャラクターシステム
**ファイル名**: `GameCharacterSystem.java`

RPGゲームのキャラクターシステムを作成してください。

**要求仕様**:
- キャラクターの基底クラス
- 職業による特性の違い
- スキルと能力の管理
- 戦闘システムの実装

**継承階層**:
```
GameCharacter (基底クラス)
├── Warrior (戦士系)
│   ├── Knight (騎士)
│   └── Berserker (狂戦士)
├── Mage (魔法使い系)
│   ├── Wizard (魔術師)
│   └── Healer (回復士)
└── Archer (弓使い系)
    ├── Hunter (狩人)
    └── Assassin (暗殺者)
```

**実行例**:
```
=== ゲームキャラクターシステム ===

⚔️ Fantasy Quest RPG

=== キャラクター作成 ===
✓ 騎士 アーサー (Lv.15)
  HP: 450/450, MP: 100/100
  攻撃力: 85, 防御力: 120, 敏捷性: 40
  特殊能力: 聖なる守り, 剣技

✓ 魔術師 マーリン (Lv.12)  
  HP: 200/200, MP: 380/380
  攻撃力: 45, 防御力: 30, 敏捷性: 60
  特殊能力: ファイアボール, テレポート

✓ 暗殺者 シャドウ (Lv.18)
  HP: 280/280, MP: 150/150  
  攻撃力: 95, 防御力: 45, 敏捷性: 140
  特殊能力: ステルス, 毒刃

=== 戦闘シミュレーション ===
バトル開始: アーサー vs ゴブリン王

Round 1:
⚔️ アーサー: 通常攻撃 → ダメージ 78
   ゴブリン王 HP: 322/400
🛡️ ゴブリン王: 爪攻撃 → ダメージ 32 (防御軽減)
   アーサー HP: 418/450

Round 2:  
✨ アーサー: 必殺技「聖なる光」→ ダメージ 156
   ゴブリン王 HP: 166/400
💀 ゴブリン王: 怒りの咆哮 → ダメージ 45
   アーサー HP: 373/450

Round 3:
⚔️ アーサー: 連続攻撃 → ダメージ 89 + 92
   ゴブリン王 HP: 0/400 (撃破!)

🏆 勝利! 経験値 +850, ゴールド +320

=== スキル成長 ===
アーサー レベルアップ! Lv.15 → Lv.16
📈 ステータス向上:
- HP: +25 (450 → 475)
- 攻撃力: +3 (85 → 88)  
- 防御力: +4 (120 → 124)

🎯 新スキル習得: 「天空剣」
   消費MP: 35, 威力: 200%, 必中攻撃

=== パーティ戦闘 ===
パーティ: アーサー, マーリン, シャドウ vs ドラゴン

マーリン:
🔥 ファイアボール → ダメージ 234
   「炎よ、敵を焼き尽くせ！」

シャドウ:  
🗡️ ステルス攻撃 → 急所ヒット ダメージ 312
   「影から這い出る死神の刃！」

アーサー:
⚔️ 天空剣 → ダメージ 176
   「聖なる力よ、我が剣に宿れ！」

🐲 ドラゴン HP: 2,078/2,800

=== キャラクター成長統計 ===
アーサー (騎士):
- 戦闘回数: 47回, 勝率: 94%
- 与ダメージ総計: 15,420
- 習得スキル: 8個

マーリン (魔術師):  
- 戦闘回数: 35回, 勝率: 89%
- 魔法使用回数: 312回
- 習得呪文: 12個

シャドウ (暗殺者):
- 戦闘回数: 52回, 勝率: 96%
- クリティカル率: 28%
- ステルス成功率: 94%
```

**評価ポイント**:
- ゲーム要素の適切な抽象化
- バランスの取れた職業設計
- 戦闘システムの実装
- 成長システムの管理

## 💡 実装のヒント

### 課題1のヒント
```java
abstract class Animal {
    protected String name;
    protected int age;
    protected String gender;
    protected double weight;
    protected boolean isHealthy;
    
    // 抽象メソッド - サブクラスで必須実装
    public abstract void makeSound();
    public abstract void move();
    public abstract String getSpecialBehavior();
    
    // 共通メソッド
    public void eat(String food, double amount) {
        System.out.println(name + "が" + food + "を" + amount + "kg食べています");
    }
    
    public void sleep() {
        System.out.println(name + "が休息しています");
    }
}

class Lion extends Mammal {
    private boolean isAlpha;
    
    @Override
    public void makeSound() {
        System.out.println("🦁 ガオー！威嚇の鳴き声");
    }
    
    @Override
    public void move() {
        System.out.println("🏃 草原を時速50kmで駆ける");
    }
    
    @Override
    public String getSpecialBehavior() {
        return isAlpha ? "群れを率いてパトロール" : "群れの仲間と協力";
    }
}
```

### 課題2のヒント
```java
abstract class Vehicle {
    protected String name;
    protected double maxSpeed;
    protected double fuelCapacity;
    protected double currentFuel;
    protected double fuelEfficiency;
    
    // 抽象メソッド
    public abstract void startEngine();
    public abstract void move(double distance);
    public abstract double calculateTravelTime(double distance);
    
    // 共通機能
    public boolean refuel(double amount) {
        if (currentFuel + amount <= fuelCapacity) {
            currentFuel += amount;
            return true;
        }
        return false;
    }
}

class Car extends LandVehicle {
    private boolean isHybrid;
    
    @Override
    public void startEngine() {
        System.out.println("🚗 エンジン始動 → " + 
            (isHybrid ? "電気モード" : "ガソリンエンジン"));
    }
    
    @Override
    public double calculateTravelTime(double distance) {
        double avgSpeed = maxSpeed * 0.8; // 平均速度
        return distance / avgSpeed;
    }
}
```

### 課題3のヒント
```java
abstract class GameCharacter {
    protected String name;
    protected int level;
    protected int hp, maxHp;
    protected int mp, maxMp;
    protected int attack, defense, agility;
    
    // 抽象メソッド
    public abstract void useSpecialSkill(GameCharacter target);
    public abstract String getCharacterClass();
    
    // 戦闘メソッド
    public int attack(GameCharacter target) {
        int damage = Math.max(1, this.attack - target.defense);
        target.takeDamage(damage);
        return damage;
    }
    
    public void takeDamage(int damage) {
        this.hp = Math.max(0, this.hp - damage);
    }
    
    public boolean isAlive() {
        return hp > 0;
    }
}

class Knight extends Warrior {
    @Override
    public void useSpecialSkill(GameCharacter target) {
        if (mp >= 35) {
            mp -= 35;
            int damage = (int)(attack * 2.0); // 天空剣
            target.takeDamage(damage);
            System.out.println(name + "の天空剣！ダメージ: " + damage);
        }
    }
}
```

## 🔍 応用のポイント

1. **継承の適切な使用**: is-a関係の正しい実装
2. **ポリモーフィズム**: 共通インターフェースによる統一的な処理
3. **抽象化**: 具象クラスで必須実装すべき機能の定義
4. **コードの再利用**: 基底クラスでの共通機能実装
5. **拡張性**: 新しいサブクラス追加の容易さ

## ✅ 完了チェックリスト

- [ ] 課題1: 動物園管理システムが正常に動作する
- [ ] 課題2: 交通手段シミュレーターが実装できた
- [ ] 課題3: ゲームキャラクターシステムが動作する
- [ ] 適切な継承階層が設計されている
- [ ] ポリモーフィズムが効果的に活用されている
- [ ] メソッドオーバーライドが適切に実装されている

**次のステップ**: 応用課題が完了したら、challenge/のチャレンジ課題に挑戦してみましょう！