import java.util.LinkedList;
import java.util.List;

import com.pengrad.telegrambot.model.Update;

import org.bson.Document;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Model implements Subject{
	
	private List<Observer> observers = new LinkedList<Observer>();
	private List<Eventos> eventos = new LinkedList<Eventos>();
	private static Model uniqueInstance;
	
	//mongo connection
	MongoClientURI uri  = new MongoClientURI("mongodb://localhost:27017/telegram"); 
    MongoClient client = new MongoClient();
	
	private Model(){}
	
	public static Model getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new Model();
		}
		return uniqueInstance;
	}
	
	public void registerObserver(Observer observer){
		observers.add(observer);
	}
	
	public void notifyObservers(long chatId, String NomeEvento){
		for(Observer observer:observers){
			observer.update(chatId, NomeEvento);
			
		}
	}
	
	
	// Adiciona evento
	public void addEvento(Update update, Eventos evento){
		this.notifyObservers(update.message().chat().id(), "O evento foi adicionado com sucesso!");
		this.eventos.add(evento);
		
	}
	// 

	public void searchEventos(Update update){
		
		this.notifyObservers(update.message().chat().id(), "Esses são os eventos adicionados");
		
		String NomeEvento = null;
//		
//		if("todos".equals(update.message().text())) {
			for(Eventos el: eventos){
				NomeEvento = el.getnomeEvento().toString();
			}
//		}else {
//			for(Eventos evento: eventos){
//				if(evento.getnomeEvento().equals(update.message().text())){
//					NomeEvento = evento.getnomeEvento().toString();
//				}
//			}
//		}
//		
		if(NomeEvento != null){
			this.notifyObservers(update.message().chat().id(), NomeEvento);
		} else {
			this.notifyObservers(update.message().chat().id(), "Nao há eventos");
		}
		
	}
	
	// mongo metodos
	 public void addBd(Update update){
	    
		Document doc = new Document().append("Evento", update.message().text() );
		
    	MongoDatabase db = client.getDatabase(uri.getDatabase());
    	MongoCollection<Document> Eventos = db.getCollection("Eventos");
    	Eventos.insertOne(doc);
    	
    	this.notifyObservers(update.message().chat().id(), "Adicionado com sucesso o evento: ");
    	this.notifyObservers(update.message().chat().id(), update.message().text());
    
	 }
	 
	 public void searchAll(Update update){
    	MongoDatabase db = client.getDatabase(uri.getDatabase());
    	MongoCollection<Document> Evento = db.getCollection("Eventos");
    	
    	if(update.message().text().equals("todos")) {
    		FindIterable<Document> found = Evento.find();
    		
        	for(Document doc:found){
        		this.notifyObservers(update.message().chat().id(), "Eventos: ");
        		this.notifyObservers(update.message().chat().id(), doc.getString("Evento"));
    		}
    	}else {
    		
    		String parse;
    		parse = String.valueOf(update.message().text());
    		System.out.println(parse);
    		FindIterable<Document> found = Evento.find(new Document("Evento", parse));
    		
        	for(Document doc:found){
        		this.notifyObservers(update.message().chat().id(), "Eventos: ");
        		this.notifyObservers(update.message().chat().id(), doc.getString("Evento"));
    		}
    	}
    	
    	
    	
	 }
	 
	 public void removeAll(Update update){
		
		Document filter = new Document();
		 
		MongoDatabase db = client.getDatabase(uri.getDatabase());
	    MongoCollection<Document> Evento = db.getCollection("Eventos");
	
	    if(update.message().text().equals("todos")) {
	    	Evento.deleteMany(filter);
	    	this.notifyObservers(update.message().chat().id(), "Eventos apagados com sucesso! ");
	    }else {
	    	String parse;
    		parse = String.valueOf(update.message().text());
    		System.out.println(parse);
	    	Document nome = new Document().append("Evento", parse);
	    	
	    	Evento.deleteOne(nome);
	    	this.notifyObservers(update.message().chat().id(), "Evento apagado com sucesso! ");
	    }
		    	
	    	
	}

}
