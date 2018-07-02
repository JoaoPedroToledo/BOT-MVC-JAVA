import com.pengrad.telegrambot.model.Update;

public class ControllerRemoveEvento implements ControllerRemove {
	
	private Model model;
	private View view;
	
	public ControllerRemoveEvento(Model model, View view){
		this.model = model; //connection Controller -> Model
		this.view = view; //connection Controller -> View
	}
	
	public void remove(Update update){
			
		view.sendTypingMessage(update);
		model.removeAll(update);
		
		
		
	}


}