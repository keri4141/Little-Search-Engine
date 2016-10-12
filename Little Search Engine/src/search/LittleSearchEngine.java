package search;

import java.io.*;
import java.util.*;


class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		
		
		// index all keywords
		
		
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		
		HashMap<String,Occurrence> temp = new HashMap<String, Occurrence>(1000,2.0f);
		
		Scanner sc = new Scanner(new File(docFile));
		//find a word
		while(sc.hasNext())
		{
			
			String word=sc.next();
			word=getKeyWord(word); //does its thang
			
			if(word==null) //if the word is null then continue le loop
			{
				continue;
			}
			
			if(temp.containsKey(word)==false) //if that key exists in the table check if the doc file matches
			{
				Occurrence occur = new Occurrence(docFile,1);
				temp.put(word, occur);
			}
			
			else
			{
				Occurrence occur=temp.get(word);
				occur.frequency++;
				temp.put(word,occur);
			}
			
		}
		

		return temp;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		
		
		for(String i: kws.keySet())
		{
		
			if(keywordsIndex.containsKey(i)==false) //if its empty
			{
				ArrayList<Occurrence> test = new ArrayList<Occurrence>();
				Occurrence recent_table_occurrence=kws.get(i); 
				test.add(recent_table_occurrence);
				insertLastOccurrence(test);
				keywordsIndex.put(i,test);
				
				
				
			}
			
			else
			{
				
				ArrayList<Occurrence> test = keywordsIndex.get(i);
				Occurrence recent_table_occurrence=kws.get(i);
				test.add(recent_table_occurrence);
				insertLastOccurrence(test);
				keywordsIndex.put(i, test);
				
				
			}
			
			
		}
		
	
		
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		int string_length=word.length();
		String NoPunc_word=word;
		boolean IndexSet=false;
		int  not_letterIndex=0;
		int before_not_letterIndex=0;
		for(int i =0;i<string_length;i++)
		{
			
			if(Character.isLetter(word.charAt(i))==false) //if its not a letter
			{
				
				
				if(i==0)
				{
					return null;
				}
				
				//if the non letter is between the end and the beginning of string
				
				
				//else was here
				
				else{
					if(IndexSet==false)
					{	
						 not_letterIndex=i;
						before_not_letterIndex=i-1;
						IndexSet=true;
					}
					//if a non letter is detected and its last index
					//ffff.? case
					
					if(not_letterIndex==string_length-1)
					{
						NoPunc_word=word.substring(0,before_not_letterIndex+1);
						break;
					}
					
					// ff???d???? case
					for(int j=not_letterIndex;j<string_length;j++)
					{
						//check if non letter is between a letter
						if(Character.isLetter(word.charAt(j))==true)
						{
							return null; //should break out of loop
						}
						
						
					}
					
					// fffffff?? case
					//if no letter is detected then it is a trailing
					 NoPunc_word=word.substring(0,before_not_letterIndex+1);
					
					
					
					
				}
				
			
				
			}
			
			
			
			
		}
		
		//lowercase the word
		NoPunc_word=NoPunc_word.toLowerCase();
		
		
		
		if(noiseWords.get(NoPunc_word)!=null)
			{
				NoPunc_word=null;
			}
		return NoPunc_word;
		
	}
	
	
	
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
	
		ArrayList<Integer> middle_array = new ArrayList<Integer>(); 
		
		
		if(occs.size()==1)
		{
			return null;
		}
		Occurrence temp = occs.get(occs.size()-1);
		occs.remove(occs.size()-1);
		
		int hi = 0; //fist index contains highest number
		int low = occs.size()-1;
		int mid=0;
		int midFrequency;
		
		
		
		while(hi<=low)
		{
			mid = (hi+low)/2;
			midFrequency=occs.get(mid).frequency;
			
			
			if(midFrequency==temp.frequency)
			{
				middle_array.add(mid);
				break;
			}
			
			if(midFrequency <temp.frequency)
			{
				low=mid-1;
				middle_array.add(mid);
			}
			
			if(midFrequency>temp.frequency)
			{
				hi=mid+1;
				middle_array.add(mid);
				mid++;

			}
		}
		
		
		occs.add(mid,temp);
		
		
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		return middle_array;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> top_five= new ArrayList<String>(5);
		
		//if both words exists
		int i=0;
		int j =0;
		int count =0;
		ArrayList<Occurrence> kw1_list = keywordsIndex.get(kw1);
		ArrayList<Occurrence> kw2_list = keywordsIndex.get(kw2);
		
		if(keywordsIndex.containsKey(kw1)==true && keywordsIndex.containsKey(kw2)==true)
		{	
			while(i<kw1_list.size() && j<kw1_list.size())
			
			{
				
				if(count>=5)
				{
					break;
				}
				
				
				
					
						if(kw1_list.get(i).frequency>kw2_list.get(j).frequency)
						{
							
							top_five.add(kw1_list.get(i).document);
							i++;
							count++;
							continue;
						}
						
						else if(kw1_list.get(i).frequency==kw2_list.get(j).frequency)
						{
							top_five.add(kw1_list.get(i).document);
							i++;
							j++;
							count++;
							continue;
						}
						
						else
						{
							top_five.add(kw1_list.get(j).document);
							j++;
							count++;
							
						}
	
				
			}
			//check which list is used up
			if(i<kw1_list.size())
			{
				for(int i2=i;i2<kw1_list.size();i2++)
				{
					if(count==5)
					{
						break;
					}
					
					top_five.add(kw1_list.get(i).document);
					count++;
				}
			}
			
			else if(j<kw1_list.size())
			{
				for(int j2=j;j2<kw1_list.size();j2++)
				{
					if(count==5)
					{
						break;
					}
					
					top_five.add(kw1_list.get(j).document);
					count++;
				}
			}
			
			
		}
		//case 2
			else if(keywordsIndex.containsKey(kw1)==true && keywordsIndex.containsKey(kw2)==false)
			{
	
				for( int t = 0;t<5;t++)
				{
					if(count==5)
					{
						
						break;
					}
					
					if(t>=kw1_list.size())
					{
						break;
					}
					top_five.add(kw1_list.get(t).document);
					count++;
				}
			}
				
			
			//if kw2 exists
			else if(keywordsIndex.containsKey(kw1)==false && keywordsIndex.containsKey(kw2)==true)
			{
				
				for( int t = 0;t<5;t++)
				{
					if(count==5)
					{
						
						break;
					}
					
					if(t>=kw2_list.size())
					{
						break;
					}
					top_five.add(kw2_list.get(t).document);
					count++;
				}
			}
			
			else
			{
				return null;
			}
		
		
		
		
		return top_five;
	}
	
	
	
}
