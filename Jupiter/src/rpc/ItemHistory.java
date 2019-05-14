package rpc;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 DBConnection connection = DBConnectionFactory.getConnection();
	  	 try {
	  		 JSONObject input = RpcHelper.readJSONObject(request);
	  		 String userId = input.getString("user_id");
	  		 JSONArray array = input.getJSONArray("favorite");
	  		 String itemId = array.getString(0);
	  		
	  		 connection.unsetFavoriteItems(userId, itemId);
	  		 RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 } finally {
	  		 connection.close();
	  	 }
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			Set<Item> items = conn.getFavoriteItems(userId);
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.append("favorite", true);
				array.put(obj);
			}
			
			RpcHelper.writeJsonArray(response, array);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// fav/unfav
		DBConnection connection = DBConnectionFactory.getConnection();
	  	try {
	  		JSONObject input = RpcHelper.readJSONObject(request);
	  		String userId = input.getString("user_id");
	  		JSONArray array = input.getJSONArray("favorite");
	  		String itemId = array.getString(0);
	  		connection.setFavoriteItems(userId, itemId);
	  		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	  	} catch (Exception e) {
	  		e.printStackTrace();
	  	} finally {
	  		connection.close();
	  	}		
	}
}
