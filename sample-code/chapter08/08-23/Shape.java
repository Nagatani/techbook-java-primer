// 図形を表すsealed interface（実装できる型をpermitsの3つに限る）
public sealed interface Shape permits Circle, Rectangle, Triangle {}
