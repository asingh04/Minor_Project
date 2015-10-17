package firstexample;
import java.sql.*;
import java.io.*;
import java.util.*;

class userqp {
    
    public int no_of_cmds;
    
    public HashSet <String> Commands=new HashSet <String>();
    
    userqp(String[] cmds,int n){
        
        no_of_cmds=n;
        for(int i=0;i<n;i++){
            Commands.add(cmds[i]);
        }
    }
}

class FirstExample {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/Project"; 
    static HashSet <String> checker=new HashSet <String>();
    static String USR;
    static String PASS;
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static Scanner input=new Scanner(System.in);
    static String cmd="";
    static int no_of_atrb=0;
    static int count; 
    static String[] U1={"SELCUS","INSCUS","SELWAR","SELNEW"};
    static String[] U1SEL={"C_ID","C_D_ID","C_W_ID",
                           "W_ID","W_NAME","W_CITY",
                           "NO_O_ID","NO_D_ID","NO_W_ID","*NEW"};
    static String[] U1INS={"C_FIRST","C_MIDDLE","C_LAST"};
    static userqp u1=new userqp(U1,U1.length);
    static userqp u1sel = new userqp(U1SEL,U1SEL.length);
    
    
    public static void main(String[] args){
        
        
        Connection conn=null;
        
        Statement stmt=null;
        
        try{
            Class.forName(JDBC_DRIVER);
            
            System.out.println("Connecting to DataBase.......");
            System.out.print("Enter username: ");
            USR=br.readLine();
            System.out.print("Enter the password: ");
            PASS=br.readLine();
            conn=DriverManager.getConnection(DB_URL,USR,PASS);
            
            System.out.println("creating statements for sql.....");
            
            stmt=conn.createStatement();
            
            String sql;
            
            int choice=1;
            while(choice!=0){
                
                System.out.println("What do you want to do?");
                System.out.println("1.Execute a Query");
                System.out.println("0.Exit");
                System.out.print("Enter choice: ");
                choice=input.nextInt();
                if(choice ==1){
                    System.out.println("Enter Query:");
                    sql=br.readLine();
                    Parsestmnttable(sql);
                    
                    if(u1.Commands.contains(cmd) && cmd!=""){
                        
                        if(Parsestmntattribute(sql)){
                        }
                        
                        else{
                            String warn="Malicious Query Intercepted!";
                            System.out.println(warn);
                            continue;
                        }
                    }
                    else{
                        String warn="Malicious Query Intercepted!";
                        System.out.println(warn);
                        continue;
                    }
                }
            }
            
            //sql=input.nextLine();
            //stmt.executeUpdate(sql);
            sql=input.nextLine();
            
            Parsestmnttable(sql);
            ResultSet rs = stmt.executeQuery(sql);
            /*
            while(rs.next()){
                
                int id=rs.getInt("id");
                int age =rs.getInt("age");
                String first=rs.getString("first");
                String last = rs.getString("last");
                //String name=rs.getString("name");
                //String city=rs.getString("city");
                System.out.print("ID = "+ id);
                System.out.print(" age = "+ age);
                System.out.print(" first = "+ first);
                System.out.println(" last = "+ last);
            }//end of while loop
            */
            rs.close();
            stmt.close();
            conn.close();
        }//end of try
        
        catch(SQLException se){
            se.printStackTrace();
        }//end of catch
        
        catch(Exception e){
            e.printStackTrace();
        }
        
        finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }//end of try
            catch(SQLException se2){
            }
            
            try{
                if(conn!=null)
                    conn.close();
            }
            catch(SQLException se3){
                se3.printStackTrace();
            }
        }//end of finally   
        System.out.println("Goodbye");
    }//end of main function
    
    public static void Parsestmnttable(String sql){
        
        String temp="";
        int p,t;
        
        if(sql.startsWith("Select") || sql.startsWith("select") || sql.startsWith("SELECT")){
            
            cmd="SEL";
            p=sql.indexOf("FROM") + 5;
        }
        
        
        else if(sql.startsWith("INSERT") || sql.startsWith("insert") || sql.startsWith("Insert")){    
            
            cmd="INS";
            p=sql.indexOf("INTO") + 5;
        }
        
        else if(sql.startsWith("UPDATE") || sql.startsWith("update") || sql.startsWith("Update")){
            
            cmd="UPD";
            p=7;
        }
        
        else if(sql.startsWith("DELETE") || sql.startsWith("delete") || sql.startsWith("Delete")){
            
            cmd="DEL";
            p=sql.indexOf("FROM") + 5;
        }
        /*
        else if(sql.startsWith("ALTER") || sql.startsWith("alter") || sql.startsWith("Alter")){
            
            cmd="ALT";
            p=sql.indexOf("table") + 6;
        }
        */
        
        else
            return;
        
        for(t=3;t!=0 && sql.charAt(p)!=' ' && sql.charAt(p)!=';';t--){
            
            temp+=sql.charAt(p);
            p++;
        }
        
        cmd+=temp;
        System.out.println(cmd);
    }//end of statment parsing
    
    
    public static boolean Parsestmntattribute(String sql){
        int index;
        String att="";
        
        if(cmd.startsWith("SEL")){
            
            index=sql.indexOf("SELECT") + 7;
            
            while(sql.charAt(index)!=' '){
                att="";
                while(sql.charAt(index)!=','&&sql.charAt(index)!=' '){
                    att+=sql.charAt(index);
                    index++;
                }
                if(sql.charAt(index)==',')
                    index++;
                
                if(att.equals("*")){
                    att+=cmd.substring(3,6);
                }
                if(u1sel.Commands.contains(att)){
                    continue;
                }
                else{
                    return false;
                }
            }
            
            if(sql.contains("WHERE")){
                index=sql.indexOf("WHERE") + 6;
                
                while(sql.charAt(index)!=';'){
                    
                    att="";
                    if(sql.charAt(index)>=48 && sql.charAt(index)<=57){ //if numeric literal
                        
                        while(sql.charAt(index)!=' ' && sql.charAt(index)!=';')
                            index++;
                    }
                    
                    else if(sql.charAt(index)==34){ //if "
                        index++;
                        while(sql.charAt(index)!=34 && sql.charAt(index)!=';')
                            index++;
                        index++;
                    }
                    
                    else if(sql.charAt(index)==39){ // if ' found
                        index++;
                        while(sql.charAt(index)!=39 && sql.charAt(index)!=';')
                            index++;
                        index++;
                    }
                    
                    else if(sql.charAt(index)=='=' || sql.charAt(index)==' '|| sql.charAt(index)=='>' || sql.charAt(index)=='<') //if = or space found
                        index++;
                    
                    else{    //then attribute name OR ANY KEYWORD IS PRESENT is present
                        
                        while(sql.charAt(index)!=' ' && sql.charAt(index)!=';')
                            att+=sql.charAt(index++);
            
                        if(att.equals("ORDER"))
                            index+=4;
                       
                        else if(att.equals("ASC") || att.equals("DESC")){
                    
                            if(sql.substring(index).startsWith(" , "))
                                index+=3;
                        }
                        else if( !att.equals("AND") && !att.equals("OR") && !att.equals("NOT") && !att.equals("BETWEEN") && !att.equals("") && !u1sel.Commands.contains(att))
                            return false;
                    }
                }
            }
            return true;
        }
        else if(cmd.substring(0,3).equals("INS")){ 
            index=sql.indexOf("SELECT") + 7;
            while(sql.charAt(index)!=' '){
                att="";
                while(sql.charAt(index)!=','&&sql.charAt(index)!=' '){
                    att+=sql.charAt(index);
                    index++;
                }
                if(sql.charAt(index)==',')
                    index++;
                
                if(att.equals("*")){
                    att+=cmd.substring(3,6);
                }
                if(u1sel.Commands.contains(att)){
                    continue;
                }
                else{
                    return false;
                }
            }
            return true;
        }
        return true;
    }//end of Parsestmntattribute
}//end of FIrstExample class 
