// catchは実行時のクラスで例外を選ぶので、ジェネリックな例外クラスは作れない
class GenericThrowable<T> extends Exception { }
