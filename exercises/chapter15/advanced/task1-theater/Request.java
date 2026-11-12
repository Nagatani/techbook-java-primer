import java.util.List;

/**
 * 1人の客の予約の申し込み
 */
public record Request(String customer, List<Integer> seats) {
}
