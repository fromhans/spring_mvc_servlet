package study.servlet.web.frontcontroller.v5;

import study.servlet.web.frontcontroller.ModelView;
import study.servlet.web.frontcontroller.MyView;
import study.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import study.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import study.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import study.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name= "frontControllerServletV5", urlPatterns="/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    /*
    private Map<String, ControllerV4> controllerMap = new HashMap<>();
    기존에는 Map에 ControllerV4가 지정됐으나, V5에서는 Object가 들어가서 아무거나 들어갈 수 있음.
     */
    private final Map<String, Object> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    /**
     * handlerMappingMap에
     * URI별 적합한 하위 컨트롤러를 URI(String)-하위컨트롤러(Object) 형태로 넣어준다.
     */
    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV5.service");

        // URI를 통해 적절한 하위 컨트롤러를 받아온다.
        Object handler = getHandler(request);

        // 매핑된 URI가 없으면 404를 반환한다.
        if(handler == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        /**
         * handler는 URI를 통해 탐색된 하위 컨트롤러 클래스가 담겨있다.
         * Adapter를 통해 handler에 있는 하위 컨트롤러 클래스가 지원되는 Adapter를 받아온다.
         * 아래 코드에선, handler에 ControllerV3의 구현체 클래스들이 들어가게 된다.
         * getHandlerAdapter()는 ControllerV3의 구현체들이 적용되는 Adapter를 List<MyHandlerAdapter> handlerAdapters에서
         * 탐색하여 받아온다.
         */
        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        /**
         * adapter는 아래 시점에 ControllerV3HandlerAdapter를 담게 된다.
         * .handle을 통해, 하위컨트롤러 구현체인 handler을 ControllerV3로 강제 형변환해주고
         * ControllerV3 구현체들의 공통되는 동작인, request의 Attribute를 뽑아 paramMap을 만들고
         * paramMap을 인자값으로 하여 하위컨트롤러들의 process()를 호출하여 비지니스 로직들을 수행한다.
         */
        ModelView mv = adapter.handle(request, response, handler);

        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);

        view.render(mv.getModel(), request, response);

    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    /**
     * initHandlerAdapters()에 의해 handlerAdapters에는 지원되는 어댑터들이 리스트형으로 담겨있다.
     * handlerAdapters를 순회하며 support되는 Adapter를 찾고, 해당 Adapter를 반환해준다.
     * @param handler
     * @return
     */
    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("handler adapter를 찾을 수 없습니다.");
    }
    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
