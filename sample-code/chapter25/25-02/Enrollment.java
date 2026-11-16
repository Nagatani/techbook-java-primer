// Enrollment.java
// 在籍区分。学部生と大学院生で持つ情報が違うので、区分ごとにレコードを分ける
public sealed interface Enrollment permits Undergraduate, Graduate {
}
