package Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.Javalin;
import java.util.List;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService = new AccountService();
    MessageService messageService = new MessageService();

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postAccountHandler);
        app.post("/login", this::postLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::patchMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByAccount_idHandler);
        return app;
    }

    /**
     * Handler to post a new account.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException Thrown if there is an issue converting JSON into an object.
     */
    private void postAccountHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account addedAccount = accountService.addAccount(account);

        if (addedAccount != null) {
            context.json(mapper.writeValueAsString(addedAccount));
        } else {
            context.status(400);
        }
    }

    /**
     * Handler to post a login request.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException Thrown if there is an issue converting JSON into an object.
     */
    private void postLoginHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account verifiedAccount = accountService.getAccountByLogin(account.getUsername(), account.getPassword());

        if (verifiedAccount != null) {
            context.json(mapper.writeValueAsString(verifiedAccount));
        } else {
            context.status(401);
        }
    }

    /**
     * Handler to post a new message.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException Thrown if there is an issue converting JSON into an object.
     */
    private void postMessageHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        Message addedMessage = messageService.addMessage(message);

        if (addedMessage != null) {
            context.json(mapper.writeValueAsString(addedMessage));
        } else {
            context.status(400);
        }
    }

    /**
     * Handler to get all Messages.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getAllMessagesHandler(Context context) {
        List<Message> messages = messageService.getAllMessages();
        context.json(messages);
    }

    /**
     * Handler to get a Message, identified by its message_id.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getMessageHandler(Context context) {
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        Message message = messageService.getMessage(message_id);
        if (message != null) {
            context.json(message);
        } else {
            context.json("");
        }
    }

    /**
     * Handler to delete a Message, identified by its message_id.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void deleteMessageHandler(Context context) {
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        Message message = messageService.deleteMessage(message_id);
        if (message != null) {
            context.json(message);
        } else {
            context.json("");
        }
    }

    /**
     * Handler to update the message_text of a Message, identified by its message_id.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException Thrown if there is an issue converting JSON into an object.
     */
    private void patchMessageHandler(Context context) throws JsonProcessingException {
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        String message_text = message.getMessage_text();

        Message updatedMessage = messageService.updateMessage(message_id, message_text);
        if (updatedMessage != null) {
            context.json(mapper.writeValueAsString(updatedMessage));
        } else {
            context.status(400);
        }
    }

    /**
     * Handler to get all messages written by a particular user, identified by their account_id.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getAllMessagesByAccount_idHandler(Context context) {
        int account_id = Integer.parseInt(context.pathParam("account_id"));

        List<Message> messages = messageService.getAllMessagesByAccount_id(account_id);
        context.json(messages);
    }
}
