// 第15章：Javaのクラスは1つのクラスしか継承できない（extendsに2つのクラスを書くとコンパイルエラー）
class Task {
}

class Worker extends Thread, Task {
}
