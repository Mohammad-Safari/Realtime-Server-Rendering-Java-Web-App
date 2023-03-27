package sfri.mhmd.rssrj.servlet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(asyncSupported = true, name = "subscriberController", urlPatterns = { "/subscriber" })
public class SubscriberController extends HttpServlet {

  private static final List<AsyncContext> asyncContexts = new CopyOnWriteArrayList<>();

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/event-stream");
    response.setCharacterEncoding("UTF-8");

    final AsyncContext asyncContext = request.startAsync(request, response);
    asyncContext.setTimeout(0);

    asyncContexts.add(asyncContext);
  }

  public static void sendEvent(final String data) {
    for (final var asyncContext : asyncContexts) {
      try (var writer = asyncContext.getResponse().getWriter();) {
        writer.write("data: " + data + "\n\n");
        writer.flush();
      } catch (final IOException e) {
        // todo Handle error
      }
    }
  }
}
