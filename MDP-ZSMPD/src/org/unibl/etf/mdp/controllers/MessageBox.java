package org.unibl.etf.mdp.controllers;

import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.model.Message;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MessageBox extends VBox {
	
	public MessageBox(Message msg) {
		this.getChildren().add(new Separator(Orientation.HORIZONTAL));
		Label sender=new Label(msg.getSender()+":");
		Label content=new Label(msg.getContent());
		content.setWrapText(true);
		HBox hBoxS=new HBox();
		HBox hBoxC=new HBox();
		hBoxS.getChildren().add(sender);
		hBoxC.getChildren().add(content);
		if(msg.getSender().equals(Main.currentUser.getUsername())){
			hBoxS.setAlignment(Pos.BASELINE_RIGHT);
			hBoxC.setAlignment(Pos.BASELINE_RIGHT);
		}else {
			hBoxS.setAlignment(Pos.BASELINE_LEFT);
			hBoxC.setAlignment(Pos.BASELINE_LEFT);
		}
		this.getChildren().add(hBoxS);
		this.getChildren().add(hBoxC);
		
		if(msg.getAttachment()!=null) {
			Label attach=new Label("+"+msg.getAttachmentName());
			HBox hBoxA=new HBox();
			hBoxA.getChildren().add(attach);
			if(msg.getSender().equals(Main.currentUser.getUsername())){
				hBoxA.setAlignment(Pos.BASELINE_RIGHT);
			}else {
				hBoxA.setAlignment(Pos.BASELINE_LEFT);
			}
			this.getChildren().add(hBoxA);
		}
		
	}

}
