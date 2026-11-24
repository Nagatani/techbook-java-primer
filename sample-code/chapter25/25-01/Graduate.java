// Graduate.java - 大学院生（研究分野と指導教員を持つ）
public record Graduate(String researchField, String advisor)
        implements Enrollment {}
