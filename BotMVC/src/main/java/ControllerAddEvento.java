import com.pengrad.telegrambot.model.Update;

public class ControllerAddEvento implements ControllerAdd {
	
	private Model model;
	private View view;
	
	public ControllerAddEvento(Model model, View view){
		this.model = model; //connection Controller -> Model
		this.view = view; //connection Controller -> View
	}
	
	public void add(Update update){
			
		view.sendTypingMessage(update);
		model.addBd(update);
		
		
		
	}


}