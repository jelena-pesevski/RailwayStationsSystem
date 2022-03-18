package org.unibl.etf.mdp.model;

import java.io.Serializable;

public class Message implements Serializable {
	
	private String sender;
	private String reciever;
	private String content;
	private String attachmentName;
	private byte[] attachment;
	
	public Message() {
		super();
	}
	
	public Message(String sender, String reciever, String content) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.content = content;
	}
	
	public Message(String sender, String reciever, String content, String attachmentName, byte[] attachment) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.content = content;
		this.attachmentName=attachmentName;
		this.attachment = attachment;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReciever() {
		return reciever;
	}

	public void setReciever(String reciever) {
		this.reciever = reciever;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

}
