import com.pengrad.telegrambot.model.Update;

public class ControllerSearchEventos implements ControllerSearch {
	
	private Model model;
	private View view;
	
	public ControllerSearchEventos(Model model, View view){
		this.model = model; //connection Controller -> Model
		this.view = view; //connection Controller -> View
	}
	
	public void search(Update update){
		view.sendTypingMessage(update);
		model.searchAll(update);
	}


	
}
