package study.servlet.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // [status-line]
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // [response-headers]
        //resp.setHeader("Content-Type", "text/plain;charset=utf-8");

        // 헤더 편의 함수를 통해 헤더 처리
        // content(resp);

        // 쿠키 처리 함수를 통해 쿠키 처리
        // cookie(resp);

        // Redirect 편의 함수
        redirect(resp);

        // 캐시 무효화
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        // 과거 버전까지 캐시를 모두 없앰
        resp.setHeader("Pragma", "no-cache");
        // 임의의 헤더 정보 추가
        resp.setHeader("my-header", "hello");

        PrintWriter writer = resp.getWriter();
        writer.println("안녕하세요");
    }

    /**
     * Redirect 편의 함수
     *
     * @param resp
     */
    private void redirect(HttpServletResponse resp) throws IOException {
        // Status Code 302
        // Location: /basic/hello-form.html
        /*        resp.setStatus(HttpServletResponse.SC_FOUND); // 302 Redirect
        resp.setHeader("Location", "/basic/hello-form.html");
        */

        resp.sendRedirect("/basic/hello-form.html");
    }


    /**
     * 헤더 편의 함수 - 컨텐츠 타입, 인코딩, 길이 등을 지정
     *
     * @param resp
     */
    private void content(HttpServletResponse resp) {
        // [response-headers]
        resp.setHeader("Content-Type", "text/plain;charset=utf-8");

        // Content-Type을 직접 정의 안하고, 아래와 같이 세팅해줄 수도 있다.
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("utf-8");

        // 컨텐츠의 길이를 지정해줄 수 있지만, 보통 자동으로 계산되서 나가기때문에 따로 세팅해줄 필요는 없다.
        //resp.setContentLength(3);
    }

    private void cookie(HttpServletResponse resp) {
        //Set-Cookie: myCookie=good; Max-age=600;
        //response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");
        // 아래와 같이 쿠키객체를 사용해서 간단하게 세팅할 수 있다.
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); // 600초
        resp.addCookie(cookie);

    }
}
