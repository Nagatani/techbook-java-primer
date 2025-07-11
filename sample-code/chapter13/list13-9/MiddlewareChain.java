/**
 * リスト13-9
 * MiddlewareChainクラス
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (322行目)
 */

public class MiddlewareChain {
    // HTTPリクエスト処理のミドルウェア
    @FunctionalInterface
    interface Middleware extends Function<Request, Response> {
        default Middleware andThen(Middleware next) {
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
        long start = System.currentTimeMillis();
        
        return Try.of(() -> Response.success())
            .andFinally(() -> {
                long duration = System.currentTimeMillis() - start;
                logger.info("Response time: {}ms", duration);
            })
            .get();
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
        .andThen(rateLimiting)
        .andThen(authenticate)
        .andThen(request -> businessLogic.handle(request));
}