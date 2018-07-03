import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

public class View implements Observer{

	
	TelegramBot bot = TelegramBotAdapter.build("478668303:AAEtBGk5L6W6H546d7uTMJSP6h20Fy3Xh5U");

	//Objeto que recebe as mensagens
	GetUpdatesResponse updatesResponse;
	//Objeto que envia as respostas
	SendResponse sendResponse;
	//Object que manda "typing"
	BaseResponse baseResponse;
			
	
	int queuesIndex=0;
	
	ControllerSearch controllerSearch; //Strategy Pattern -- connection View -> Controller
	ControllerSearch controllerSearchEventos;
	ControllerAdd controllerAdd;
	ControllerRemove controllerRemove;

	int searchBehaviour = 0;
	
	private Model model;
	
	public View(Model model){
		this.model = model; 
	}
	
	public void setControllerSearch(ControllerSearch controllerSearch){ //Strategy Pattern
		this.controllerSearch = controllerSearch;
	}
	
//	public void setControllerSearchEventos(ControllerSearch controllerSearchEventos){
//		this.controllerSearch = controllerSearchEventos;
//	}

	public void setControllerAdd(ControllerAdd controllerAdd){ //Strategy Pattern
		this.controllerAdd = controllerAdd;
	}
	
	public void setControllerRemove(ControllerRemove controllerRemove){ //Strategy Pattern
		this.controllerRemove = controllerRemove;
	}
	
	public void receiveUsersMessages() {
		
		//loop infintito
		while (true){
		
			//taking the Queue of Messages
			updatesResponse =  bot.execute(new GetUpdates().limit(100).offset(queuesIndex));
			
			//Queue of messages
			List<Update> updates = updatesResponse.updates();

			//taking each message in the Queue
			for (Update update : updates) {
				
				//updating queue's index
				queuesIndex = update.updateId()+1;
				
				if(this.searchBehaviour==1){
					this.callControllerAdd(update);	
				}
				else if (this.searchBehaviour==2) {
					this.callControllerSearch(update);
				}
				else if (this.searchBehaviour==3) {
					this.callControllerRemove(update);
				}
				
				else if(update.message().text().equals("Adicionar")){
					setControllerAdd(new ControllerAddEvento(model, this));
					sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Digite o nome do evento e o dia"));
					this.searchBehaviour = 1;
				}
				
				else if(update.message().text().equals("Listar")){
					setControllerSearch(new ControllerSearchEventos(model, this));
					sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Digite o nome do evento que deseja procurar ou se desejar listar todos digite todos"));
					this.searchBehaviour = 2;
					
					
				}
				else if(update.message().text().equals("Remover")){
					setControllerRemove(new ControllerRemoveEvento(model, this));
					sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Digite o nome do evento que deseja remover ou se desejar remover todos digite todos"));
					this.searchBehaviour = 3;
					
					
				}else {
					sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Ola " + update.message().chat().firstName() + "\nDigite:\nAdicionar(para adicionar um evento), \nListar(para procurar um evento),\nRemover(para remover um evento)"));
				}
			
			}

		}
				
	}
	
	public void callControllerSearch(Update update) {
		this.controllerSearch.search(update);
	}

	public void callControllerAdd(Update update){
			this.controllerAdd.add(update);
	}
	
	public void callControllerRemove(Update update){
		this.controllerRemove.remove(update);
	
	}
	
	public void update(long chatId, String studentsData){
		sendResponse = bot.execute(new SendMessage(chatId, studentsData));
		this.searchBehaviour = 0;
	}
	
	public void sendTypingMessage(Update update){
		baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
	}
	

}