package com.icodejava.research.nlp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.icodejava.research.nlp.domain.NGram;
import com.icodejava.research.nlp.domain.NGramType;
import com.icodejava.research.nlp.domain.Sentence;

public class SentencesUnreferencedDB extends DBUtility {

    public static final String DATABASE_URL = "jdbc:sqlite:E:/NLP_DB/npl3.db";//SHADOWED FROM PARENT
	
	public static void main(String args []) {
		//createSentencesUnreferencedTable();
		//selectAllRecords();
		//selectSentencesWithLengthGreaterThan(50);
		//selectRecordsBetweenIds(1,10);
		//selectUnverifiedSentencesRandom(10,5,20);
		//selectRandomRecords(20);
	}
	
	public static void createSentencesUnreferencedTable() {
		String sql = "CREATE TABLE IF NOT EXISTS "+ Tables.SENTENCES_UNREFERENCED +" (\n" + " ID integer PRIMARY KEY AUTOINCREMENT,\n"
				 + " SENTENCE text NOT NULL,\n"
				 + " LINKED_WORD_EXTRACT_UNREF CHAR(1),\n"
				 + " VERIFIED CHAR(1)\n" + ");";

		createNewTable(DATABASE_URL,sql);
	}
	

	public static List<Sentence> selectUnverifiedSentencesRandom(int limit, int minWordCount, int maxWordCount) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" WHERE ID IN "
				+ "(SELECT ID FROM " + Tables.SENTENCES_UNREFERENCED + " WHERE VERIFIED IS NOT 'Y' AND WORDS_COUNT BETWEEN " + minWordCount + 
				" AND " + maxWordCount +" ORDER BY RANDOM() LIMIT " + limit +");";
		
		System.out.println(sql);

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String sentenceString = rs.getString("SENTENCE");

                if (!sentenceString.contains("'") 
                        && !sentenceString.contains("\"") 
                        && !sentenceString.contains("‘") 
                        && !sentenceString.contains("’") 
                        && !sentenceString.contains("|") 
                        && !sentenceString.contains(".") 
                        && !sentenceString.contains("…") 
                        && !sentenceString.contains("“") 
                        && !sentenceString.contains("”") 
                        && !sentenceString.contains("धेरैले मन पराएको कमेन्ट") 
                        && !sentenceString.contains("”")) { // exclude
                                                                                                                                                                                          // sentencs
                                                                                                                                                                                          // containing
                    // single quote and double
                    // quotes.
                    Sentence sentence = new Sentence(rs.getInt("ID"), rs.getString("SENTENCE"));
                    sentences.add(sentence);
                    // System.out.println(sentence);
                }
            }
        } catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return sentences;
		
	}
	
	
	public static void deleteRecordsByID(int id) {

		String sql = "DELETE FROM "+ Tables.SENTENCES_UNREFERENCED +" WHERE ID=" + id;

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.executeUpdate();

			System.out.println("Successfully Deleted record: " + id);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}
	
	public static List<Sentence> selectAllRecords() {
		List<Sentence> sentences = new ArrayList<Sentence>();
		
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" ORDER BY SENTENCE ASC";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Sentence sentence = new Sentence(rs.getInt("ID"), rs.getString("SENTENCE"));
				sentence.setVerified(rs.getString("VERIFIED"));
				sentence.setNGramsExtracted("Y".equalsIgnoreCase(rs.getString("LINKED_WORD_EXTRACT_UNREF")));
				//System.out.println(rs.getInt("ID") + "\t" + rs.getString("SENTENCE") + "\t" + rs.getString("VERIFIED") + "\t" + rs.getString("LINKED_WORD_EXTRACT_UNREF"));
				
				sentences.add(sentence);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return sentences;
	}
	
	public static List<Sentence> selectRecordsWithLimit(int limit) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" ORDER BY SENTENCE ASC "+ " LIMIT " + limit;
		
		System.out.println("sql");

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Sentence sentence = new Sentence(rs.getInt("ID"), rs.getString("SENTENCE"));
				sentence.setVerified(rs.getString("VERIFIED"));
				sentence.setNGramsExtracted("Y".equalsIgnoreCase(rs.getString("LINKED_WORD_EXTRACT_UNREF")));
				System.out.println(rs.getInt("ID") + "\t" + rs.getString("SENTENCE") + "\t" + rs.getString("VERIFIED") + "\t" + rs.getString("LINKED_WORD_EXTRACT_UNREF"));
				
				sentences.add(sentence);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Fetched: " + sentences.size() + " records");
		
		return sentences;
	}
	
	public static void selectRecordCountByLength() {
		String sql = "SELECT Length(SENTENCE), count (*) FROM " +  Tables.SENTENCES_UNREFERENCED +" GROUP BY LENGTH(SENTENCE) ORDER BY 1 DESC";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				System.out.println(rs.getString(1)+" " + rs.getString(2));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public static List<Sentence> selectRecordsWithNoWordCount(int limit) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" WHERE WORDS_COUNT IS NULL LIMIT "+limit;

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Sentence sentence = new Sentence(rs.getInt("ID"), rs.getString("SENTENCE") );
				sentences.add(sentence);
				
				//System.out.println(sentence.getId() + " " + sentence.getValue());
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return sentences;
	}
	
	
	
	
	public static boolean alreadyExists(String sentence) {
		boolean alreadyExists = false;
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" WHERE SENTENCE=\"" + sentence + "\"";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				//System.out.println("Sentence Already Exists in "+ Tables.WORDS_UNREFERENCED);
				alreadyExists = true;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return alreadyExists;
	}
	
	public static int insertSentence(String sentence) {
		int rowID = -1;
		if (alreadyExists(sentence)) {
			return rowID;
		}

		String sql = "INSERT INTO "+ Tables.SENTENCES_UNREFERENCED + " (SENTENCE) VALUES(?)";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, sentence);
			pstmt.executeUpdate();

			// Get Article ID back
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select seq from sqlite_sequence where name=\"" + Tables.SENTENCES_UNREFERENCED + "\"");
			
			if(rs.getInt(1) < 1) {
			    System.out.println("There was some issue inserting the sentence " + sentence);
			}

			//System.out.println("Inserted Record ID: " + (rowID = rs.getInt(1)));

		} catch (Exception e) {
			//GRACEFUL
			e.printStackTrace();
		}

		return rowID;
	}
	
	
	public static void updateSentence(int id, String sentenceNewValue) {

		String sql = "UPDATE " + Tables.SENTENCES_UNREFERENCED + " SET SENTENCE=? WHERE ID=?";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, sentenceNewValue);
			pstmt.setInt(2, id);
			int result = pstmt.executeUpdate();

			if(result > 0) {
				//System.out.println("Successfully updated sentence");
			} else {
				//System.out.println("Could not update the sentence. Make sure the ID exists or there are no other issues");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public static void selectSentencesWithLengthGreaterThan(int length) {
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" where LENGTH(SENTENCE) > " + length + " ORDER BY 1 DESC";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				System.out.println(rs.getString(1)+" " + rs.getString(2));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method updates a list of Sentences to the database, if the sentence value was modified.
	 * 
	 * Shorter sentences that carry less to no value are deleted.
	 * @param sentences
	 */
	public static void updateSentences(List<Sentence> sentences) {
		if(sentences == null || sentences.isEmpty()) {
			return;
		}
		
		for(Sentence sentence:sentences) {
			
			/*if(sentence == null || sentence.getValue() == null) {
				continue;
			}*/
			
			if(sentence.getValue().trim().length() < 3) { //not a possible sentence. Ma chhu is 4 character.
				deleteRecordsByID(sentence.getId());
				System.out.println("Deleted Record: " + sentence.getId());
			} else if (sentence.isModified()){
				updateSentence(sentence.getId(), sentence.getValue());
				System.out.println("Updated Record " + sentence.getId());
			} else {
				System.out.println("Not Modified Record " + sentence.getId());
			}
		}
		
	}

	public static void updateSentencesWordCount(List<Sentence> sentences) {
		if(sentences == null || sentences.isEmpty()) {
			return;
		}
		
		for(Sentence sentence:sentences) {
			
			if(sentence.getWordCount() > 0) {
				updateSentenceWordCount(sentence.getId(), sentence.getWordCount());
			}
		}
		
	}

	private static void updateSentenceWordCount(int id, int wordCount) {
		String sql = "UPDATE " + Tables.SENTENCES_UNREFERENCED + " SET WORDS_COUNT=? WHERE ID=?";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, wordCount);
			pstmt.setInt(2, id);
			int result = pstmt.executeUpdate();

			if(result > 0) {
				//System.out.println("Successfully updated word count for the sentence");
			} else {
				//System.out.println("Could not update the sentence. Make sure the ID exists or there are no other issues");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
	
	/**
	 * This method removes duplicate sentences from the database
	 * @throws InterruptedException
	 */
	public static void removeDuplicateSentences() throws InterruptedException {
		//Query Database And get a list of duplicate sentences
		String sql = "select sentence, count(*) from " + Tables.SENTENCES_UNREFERENCED + " group by SENTENCE having count(*) > 1";
		List<String> sentencesList = new ArrayList<String> ();
		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				sentencesList.add(rs.getString("SENTENCE"));
			}
			
			System.out.println("Total " + sentencesList.size() + " sentences found with duplicate entries" );
			Thread.sleep(5000); //sleeping for 5 seconds to allow change of mind.
			
			for(String sentence: sentencesList) {
				
				//Fetch the IDs of the sentence
				List<Sentence> sentences = selectSentenceBySentenceValue(sentence);
				
				if(sentences.size() > 1) { //duplicate
					
					for(int i=1; i< sentences.size(); i++) { //starting from 1 - don't delete the first record
						
						//Delete the sentence
						if(sentences.get(i).getId() > 0) {
						 
							deleteRecordsByID(sentences.get(i).getId());
						}
					}
				}
				
				
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		
	}
	
	
	/**
	 * Queries the database and gets a list of words by Word Value
	 * @param sentenceValue
	 * @return
	 */
	private static List<Sentence> selectSentenceBySentenceValue(String sentenceValue) {
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" WHERE SENTENCE=\""+sentenceValue+ "\" ORDER BY SENTENCE ASC";

		List<Sentence> sentences = new ArrayList<Sentence>();
		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				
				Sentence sentence = new Sentence(rs.getInt("ID"), rs.getString("SENTENCE"));
				sentences.add(sentence);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return sentences;
	}
	
	
	
	public static List<Sentence> selectRecordsBetweenIds(int start, int end) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		
		 //SELECT * FROM table WHERE id IN (SELECT id FROM table ORDER BY RANDOM() LIMIT x)
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" WHERE ID BETWEEN " + start + " AND " + end;

		System.out.println(sql);
		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Sentence sentence = new Sentence(rs.getInt("ID"), rs.getString("SENTENCE"));
				sentences.add(sentence);
				
				System.out.println(sentence);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return sentences;
	}

	public static void updateSentenceVerificationStatus(int id, String verified) {
		String sql = "UPDATE " + Tables.SENTENCES_UNREFERENCED + " SET VERIFIED=? WHERE ID=?";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, verified);
			pstmt.setInt(2, id);
			int result = pstmt.executeUpdate();

			if(result > 0) {
				//System.out.println("Successfully updated sentence");
			} else {
				//System.out.println("Could not update the sentence. Make sure the ID exists or there are no other issues");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	public static int getVerifiedSentenceCount() {
		int count = 0;
		String sql = "SELECT count (*) FROM " +  Tables.SENTENCES_UNREFERENCED +" WHERE VERIFIED = \"Y\"";

		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return count;
	}
	
	
	public static void selectRandomRecords(int limit) {
		
		String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" WHERE ID IN (SELECT ID FROM " + Tables.SENTENCES_UNREFERENCED +" ORDER BY RANDOM()  LIMIT " + limit + ") ORDER BY SENTENCE ASC";

		System.out.println(sql);
		try (Connection conn = DriverManager.getConnection(DATABASE_URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				System.out.println(rs.getInt("ID") + "\t" + rs.getString("SENTENCE") + "\t" + rs.getString("VERIFIED"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

    public static List<Sentence> getVerifiedSentencesNgramNotCreated(int sentenceLimit) {
        String sql = "SELECT * FROM " +  Tables.SENTENCES_UNREFERENCED +" WHERE ID IN (SELECT ID FROM " + Tables.SENTENCES_UNREFERENCED +" WHERE (NGRAM_EXTRACTED IS NULL OR NGRAM_EXTRACTED=\'N\') AND VERIFIED=\'Y\' ORDER BY RANDOM()  LIMIT " + sentenceLimit + ") ORDER BY SENTENCE ASC";

        //System.out.println(sql);
        
        List<Sentence> sentences = new ArrayList<Sentence>();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                
                Sentence sentence = new Sentence(rs.getInt("ID"), rs.getString("SENTENCE"));
                sentences.add(sentence);
                
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        System.out.println("Found "  + sentences.size() + " Sentences");
        return sentences;
        
    }

    public static void markNGramExtracted(Connection conn,Sentence sentence) {
        String sql = "UPDATE " + Tables.SENTENCES_UNREFERENCED + " SET NGRAM_EXTRACTED=?, UPDATED_DATE=? WHERE ID=?";

        try (//Connection conn = (conn1!=null)?conn1:DriverManager.getConnection(DATABASE_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "Y");
            pstmt.setDate(2, new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
            pstmt.setInt(3, sentence.getId());
            int result = pstmt.executeUpdate();

            if(result > 0) {
                //System.out.println("Successfully updated sentence " + sentence.getId());
            } else {
                System.out.println("Could not update the sentence. Make sure the ID exists or there are no other issues");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void storeNGrams(List<Sentence> sentences) {
        try (Connection conn = NGramsDB.getConnection();) {
            int count = 1;
            for (Sentence sentence : sentences) {
                System.out.print (count + " of " + sentences.size() +" ");

                NGramsDB.insertOrUpdateNGrams(conn, sentence.getnGrams());
                SentencesUnreferencedDB.markNGramExtracted(conn, sentence);
                
                count++;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static List<Sentence> searchForSentences(String searchTerm, String searchType, int searchLimit) {
        List<Sentence> searchResult = new ArrayList<Sentence>();
        String sql = "SELECT * FROM " + Tables.SENTENCES_UNREFERENCED + " WHERE SENTENCE LIKE ? LIMIT ?";

        try (Connection conn = getConnection(); 
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String wordsLike = "%" + searchTerm + "%"; // anywhere

            if ("start".equalsIgnoreCase(searchType)) {
                wordsLike = searchTerm + "%";
            } else if ("end".equalsIgnoreCase(searchType)) {
                wordsLike = "%" + searchTerm;
            }

            pstmt.setString(1, wordsLike);
            pstmt.setInt(2, searchLimit);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

             
                    Sentence sentence = new Sentence();
                    sentence.setId(rs.getInt("ID"));
                    sentence.setValue(rs.getString("SENTENCE"));
    
                    sentence.setVerified(rs.getString("VERIFIED"));
    
                    searchResult.add(sentence);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchResult;
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

}
