import java.io.File;
import java.io.IOException;
import java.io.StringWriter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.stringtemplate.v4.*;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

@SuppressWarnings("unused")


class Namerqaggr {
	String namerq, nameaggr;
	Namerqaggr(final String nrq, final String naggr)
	{ namerq=nrq; nameaggr=naggr; }
	String getNamerq() {return namerq; }
	String getNameaggr() {return nameaggr; }
}


public class rqwst0 {
	

	public static void onequery(String queryfileString, String stvarname, ST st) {
        Query query = QueryFactory.read(queryfileString) ;

        QueryExecution qexec = QueryExecutionFactory.create(query, ModelFactory.createDefaultModel()) ;

        // see http://jena.apache.org/documentation/javadoc/arq/com/hp/hpl/jena/query/Query.html
        try {
            ResultSet rs = qexec.execSelect() ;
            List<String> myvars;
			myvars = rs.getResultVars();
			int Ncolumns = myvars.size();

			
	        // See http://www.antlr.org/wiki/display/ST4/Introduction, Injecting data aggregate attributes
			// Should be able to make this more clever by storing result of query in object that can be accessed by Stringtemplate
			// See Accessing properties of model objects in http://www.antlr.org/wiki/display/ST4/Introduction
			String myaggrSpec=stvarname; 
			String delim=".{";
			for(String v : myvars){
				myaggrSpec= myaggrSpec+delim+v;
				delim=","; 
			}
			myaggrSpec= myaggrSpec+"}";


			// Array of objects to store row from querysolution
			Object[] Olist= new Object[Ncolumns];
			
            //Could also adapt one of ResultSetFormatter.out(System.out, rs, query) ;
			// See jena/jena-arq/src/main/java/com/hp/hpl/jena/sparql/resultset/CSVOutput.java
			// int i=0;  // row counter
			int j=0;  // column counter
            for ( ; rs.hasNext() ; )
            {
            	QuerySolution rb = rs.nextSolution() ;
                
            	j=0;
    			for(String v : myvars){
    				RDFNode x = rb.get(v) ;
                
    				// Check the type of the result value
    				if ( x instanceof Literal )
    				{
    					Literal l = rb.getLiteral(v) ;
    					Olist[j]=l.getLexicalForm();
    				}
    				else
    				{
    					if ( x==null) {
    						Olist[j]= "";
    					} else {
    						Olist[j]= x.toString();
    					}
    				}
    				j+=1;                
    			}
    			
    			st.addAggr(myaggrSpec, Olist );
    			// i+=1;
            }
        } finally {
            qexec.close() ;
        }

    	
    };
    
    
	public static void main(String[] args) {

		// Use a logger configuration file
		//        PropertyConfigurator.configure("c:/opt/apache-jena-2.11.0/jena-log4j.properties");
		// Alternative ref http://stackoverflow.com/questions/4570072/how-to-configure-log4j-properties-for-springjunit4classrunner
		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.INFO);
		rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%-6r [%p] %c - %m%n")));
		
		ArrayList<Namerqaggr> rqstdef = new ArrayList<Namerqaggr>();
		STGroup group=null;
		ST st = null;
		
//		STGroup group = new STGroupFile("rqwst0-example.stg");
//		ST st = group.getInstanceOf("showrqs"); 
		/*
        Namerqaggr a1= new Namerqaggr("sdtm-ae.rq", "ae");
		Namerqaggr a2= new Namerqaggr("sdtm-dm.rq", "dm");
		rqstdef.add( a1);
		rqstdef.add( a2 );
*/
		
		// --query sdtm-ae.rq ae sdtm-dm.rq dm --stringtemplate rqwst0-example.stg showrqs 

        int p=0;
        String rqname, stname;
        if (args[p].equals("--query")) {
            p+=1;
        for (; p<args.length && args[p].charAt(0) != '-'; p+=1) {
        	rqname=args[p];
        	stname=args[p+1];
        	if (stname.endsWith(".rq") || stname.charAt(0) == '-') {
        		File f = new File(rqname); 
        		stname=(f.getName()).substring(0, (f.getName()).lastIndexOf('.'));
        		} 
        	else 
        	{p+=1;}      	
            Namerqaggr a1= new Namerqaggr(rqname, stname);
    		rqstdef.add( a1);
        }
        }
        
        if (args[p].equals("--stringtemplate")) {
        	p+=1;
        	String groupname=args[p];
    		group = new STGroupFile(groupname);
        	p+=1;
        	String stinstn;
        	if (p<args.length) {
            	stinstn=args[p];
        	} else{
        		File f = new File(groupname); 
        		stinstn=(f.getName()).substring(0, (f.getName()).lastIndexOf('.'));
        		} 
    		st = group.getInstanceOf(stinstn);
            }
        
	    for (ListIterator<Namerqaggr> it = rqstdef.listIterator(); it.hasNext(); ) {
	    	Namerqaggr rqst= it.next();
			onequery( rqst.getNamerq(), rqst.getNameaggr(), st);
	    }
	    
		
        System.out.println(st.render());
	}
}
