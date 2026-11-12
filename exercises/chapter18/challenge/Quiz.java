import java.util.ArrayList;
import java.util.List;

/**
 * 制限時間つきクイズの進行（GUIに依存しない）。
 * 1問ごとに「出題中」→「回答済み」→ next()で次の問題、をくり返す。
 */
public class Quiz {
    private final List<Question> questions;
    private final int timeLimitSeconds;
    private final List<Result> results = new ArrayList<>();
    private int currentIndex;
    private int remainingSeconds;
    private boolean answered;

    public Quiz(List<Question> questions, int timeLimitSeconds) {
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("問題がありません");
        }
        this.questions = List.copyOf(questions);
        this.timeLimitSeconds = timeLimitSeconds;
        this.remainingSeconds = timeLimitSeconds;
    }

    /** 最初の問題からやり直す */
    public void restart() {
        results.clear();
        currentIndex = 0;
        remainingSeconds = timeLimitSeconds;
        answered = false;
    }

    public Question currentQuestion() {
        return questions.get(currentIndex);
    }

    /** 選択肢を選ぶ。出題中でなければnullを返す */
    public Result answer(int choiceIndex) {
        if (answered || isFinished()) {
            return null;
        }
        Result result = (choiceIndex == currentQuestion().answerIndex())
                ? Result.CORRECT : Result.WRONG;
        record(result);
        return result;
    }

    /** 1秒たったときに呼ぶ。時間切れになったらTIME_UPを、それ以外はnullを返す */
    public Result tick() {
        if (answered || isFinished()) {
            return null;
        }
        remainingSeconds--;
        if (remainingSeconds <= 0) {
            record(Result.TIME_UP);
            return Result.TIME_UP;
        }
        return null;
    }

    private void record(Result result) {
        results.add(result);
        answered = true;
    }

    /** 次の問題へ進む。回答済みのときだけ進める */
    public boolean next() {
        if (!answered || isLastQuestion()) {
            return false;
        }
        currentIndex++;
        remainingSeconds = timeLimitSeconds;
        answered = false;
        return true;
    }

    public boolean isAnswered() {
        return answered;
    }

    public boolean isLastQuestion() {
        return currentIndex == questions.size() - 1;
    }

    /** 最後の問題に答え終わったか */
    public boolean isFinished() {
        return results.size() == questions.size();
    }

    /** 1問目に答える前でも、終わった後でもなければ途中 */
    public boolean isInProgress() {
        return !isFinished() && (currentIndex > 0 || answered);
    }

    public int getCurrentNumber() {
        return currentIndex + 1;
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public int getScore() {
        int score = 0;
        for (Result r : results) {
            if (r == Result.CORRECT) {
                score++;
            }
        }
        return score;
    }

    public List<Result> getResults() {
        return List.copyOf(results);
    }
}
