// オーバーライドで公開範囲を狭めることはできない（親の型で呼べたものが呼べなくなるため）
class WeakerAccess {
    static class Base {
        public void run() {}
    }

    static class Child extends Base {
        @Override
        void run() {}
    }
}
