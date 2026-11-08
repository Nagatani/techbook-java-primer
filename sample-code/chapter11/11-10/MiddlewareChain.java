import java.util.function.Function;

public class MiddlewareChain {
    private final Logger logger = new Logger();
    private final TokenService tokenService = new TokenService();
    private final RateLimiter rateLimiter = new RateLimiter(3);
    private final BusinessLogic businessLogic = new BusinessLogic();

    // HTTPリクエスト処理のミドルウェア
    @FunctionalInterface
    interface Middleware extends Function<Request, Response> {
        default Middleware then(Middleware next) {
            return request -> {
                Response response = this.apply(request);
                if (response.isSuccess()) {
                    return next.apply(request);
                }
                return response;
            };
        }
    }
    
    // 認証ミドルウェア
    Middleware authenticate = request -> {
        String token = request.getHeader("Authorization");
        if (tokenService.isValid(token)) {
            request.setAttribute("user", tokenService.getUser(token));
            return Response.success();
        }
        return Response.unauthorized();
    };
    
    // ロギングミドルウェア
    Middleware logging = request -> {
        logger.info("Request: {} {}", request.getMethod(), request.getPath());
        return Response.success();
    };
    
    // レート制限ミドルウェア
    Middleware rateLimiting = request -> {
        String clientId = request.getClientId();
        if (rateLimiter.tryAcquire(clientId)) {
            return Response.success();
        }
        return Response.tooManyRequests();
    };
    
    // ミドルウェアチェーンの構築
    Middleware pipeline = logging
        .then(rateLimiting)
        .then(authenticate)
        .then(request -> businessLogic.handle(request));

    public static void main(String[] args) {
        MiddlewareChain chain = new MiddlewareChain();
        System.out.println(chain.pipeline.apply(new Request("GET", "/orders", "client1", "valid-token")));
        System.out.println(chain.pipeline.apply(new Request("GET", "/orders", "client1", "bad-token")));
        System.out.println(chain.pipeline.apply(new Request("GET", "/orders", "client1", "valid-token")));
        System.out.println(chain.pipeline.apply(new Request("GET", "/orders", "client1", "valid-token")));
    }
}
