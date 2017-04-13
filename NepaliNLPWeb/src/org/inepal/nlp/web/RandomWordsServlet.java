package org.inepal.nlp.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.icodejava.research.nlp.database.WordsUnreferencedDB;
import com.icodejava.research.nlp.domain.Word;

/**
 * Servlet implementation class RandomWordsServlet
 */
@WebServlet("/RandomWordsServlet")
public class RandomWordsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RandomWordsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			WordsUnreferencedDB.selectRandomRecords(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getWords(request);

		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String nextJSP = "jsp/index.jsp?result=successful";
//		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
//		dispatcher.forward(request,response);
		response.sendRedirect(nextJSP);
		
	}

	private List<Word> getWords(HttpServletRequest request) {
		Enumeration params = request.getParameterNames();
		List<Word> words = new ArrayList<Word>();
		for (; params.hasMoreElements();) {
			// Get the name of the request parameter
			String name = (String) params.nextElement();
			System.out.println(name);
			if (name.startsWith("word_")) {

				int id = new Integer(name.substring(name.lastIndexOf("_") + 1).trim());
				System.out.println(id);

				// Get the value of the request parameter
				String value = request.getParameter(name);

				Word word = getWordById(words, id);
				if (word== null) {
					word = new Word(id, null, null);
				}

				if (name.startsWith("word_root_")) {
					word.setIsCompoundWord("N");
				} else if (name.startsWith("word_derived_")) {
					word.setIsCompoundWord("Y");
				} else if (name.startsWith("word_person_")) {
					word.setClassfication1("Person");
				} else if (name.startsWith("word_firstname_")) {
					word.setClassfication2("First Name");
				} else if (name.startsWith("word_surname_")) {
					word.setClassfication3("Surname");
				} else if (name.startsWith("word_location_")) {
					word.setClassfication1("Place");
				} else if (name.startsWith("word_english_")) {
					word.setClassfication5("Person");
				} else if (name.startsWith("word_sports_")) {
					word.setClassfication1("Sports");
				} else if (name.startsWith("word_politics_")) {
					word.setClassfication1("Politics");
				}

				words.add(word);

				System.out.println("Found: " + name + " " + value);
			}

		}
		return words;

	}

	private Word getWordById(List<Word> words, int id) {
		Word aWord = null;
		for(Word word:words) {
			if (word.getId() == id) {
				aWord = word;
				break;
			}
		}
		return aWord;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	

}
