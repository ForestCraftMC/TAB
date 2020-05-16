package me.neznamy.tab.shared.packets;

import java.util.*;

import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.neznamy.tab.platforms.bukkit.packets.method.MethodAPI;
import me.neznamy.tab.shared.ProtocolVersion;
import me.neznamy.tab.shared.placeholders.Placeholders;

@SuppressWarnings({"unchecked", "unused"})
public class IChatBaseComponent {

	public static final String EMPTY_SIGNED_COMPONENT = "{\"signature\":\"TAB\",\"translate\":\"\"}";

	private String text;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private EnumChatFormat color;
	private ClickAction clickAction;
	private Object clickValue;
	private HoverAction hoverAction;
	private String hoverValue;
	private List<IChatBaseComponent> extra;
	private String signature;

	private JSONObject jsonObject = new JSONObject();

	public IChatBaseComponent() {
	}
	public IChatBaseComponent(String text) {
		setText(text);
	}

	public List<IChatBaseComponent> getExtra(){
		return extra;
	}
	public IChatBaseComponent setExtra(List<IChatBaseComponent> components){
		this.extra = components;
		jsonObject.put("extra", extra);
		return this;
	}
	public IChatBaseComponent addExtra(IChatBaseComponent child) {
		if (extra == null) {
			extra = new ArrayList<IChatBaseComponent>();
			jsonObject.put("extra", extra);
		}
		extra.add(child);
		return this;
	}

	public String getText() {
		return text;
	}
	public EnumChatFormat getColor() {
		return color;
	}
	public boolean isBold(){
		return bold == null ? false : bold;
	}
	public boolean isItalic(){
		return italic == null ? false : italic;
	}
	public boolean isUnderlined(){
		return underlined == null ? false : underlined;
	}
	public boolean isStrikethrough(){
		return strikethrough == null ? false : strikethrough;
	}
	public boolean isObfuscated(){
		return obfuscated == null ? false : obfuscated;
	}

	public IChatBaseComponent setText(String text) {
		this.text = text;
		jsonObject.put("text", text);
		return this;
	}
	public IChatBaseComponent setColor(EnumChatFormat color) {
		this.color = color;
		jsonObject.put("color", color.toString().toLowerCase());
		return this;
	}
	public IChatBaseComponent setBold(Boolean bold) {
		this.bold = bold;
		jsonObject.put("bold", bold);
		return this;
	}
	public IChatBaseComponent setItalic(Boolean italic) {
		this.italic = italic;
		jsonObject.put("italic", italic);
		return this;
	}
	public IChatBaseComponent setUnderlined(Boolean underlined) {
		this.underlined = underlined;
		jsonObject.put("underlined", underlined);
		return this;
	}
	public IChatBaseComponent setStrikethrough(Boolean strikethrough) {
		this.strikethrough = strikethrough;
		jsonObject.put("strikethrough", strikethrough);
		return this;
	}
	public IChatBaseComponent setObfuscated(Boolean obfuscated) {
		jsonObject.put("obfuscated", obfuscated);
		this.obfuscated = obfuscated;
		return this;
	}

	public IChatBaseComponent onClickOpenUrl(String url) {
		return onClick(ClickAction.OPEN_URL, url);
	}
	public IChatBaseComponent onClickRunCommand(String command) {
		return onClick(ClickAction.RUN_COMMAND, command);
	}
	public IChatBaseComponent onClickSuggestCommand(String command) {
		return onClick(ClickAction.SUGGEST_COMMAND, command);
	}
	public IChatBaseComponent onClickChangePage(int newpage) {
		return onClick(ClickAction.CHANGE_PAGE, newpage);
	}
	private IChatBaseComponent onClick(ClickAction action, Object value) {
		clickAction = action;
		clickValue = value;
		JSONObject click = new JSONObject();
		click.put("action", action.toString().toLowerCase());
		click.put("value", value);
		jsonObject.put("clickEvent", click);
		return this;
	}

	public IChatBaseComponent onHoverShowText(String text) {
		return onHover(HoverAction.SHOW_TEXT, text);
	}
	public IChatBaseComponent onHoverShowItem(ItemStack item) {
		return onHover(HoverAction.SHOW_ITEM, MethodAPI.getInstance().serialize(item));
	}
	public IChatBaseComponent onHoverShowEntity(UUID id, String customname, String type) {
		JSONObject json = new JSONObject();
		json.put("id", id.toString());
		if (type != null) json.put("type", type);
		if (customname != null) json.put("name", customname);
		return onHover(HoverAction.SHOW_ENTITY, json.toString());
	}
	private IChatBaseComponent onHover(HoverAction action, String value) {
		hoverAction = action;
		hoverValue = value;
		JSONObject hover = new JSONObject();
		hover.put("action", action.toString().toLowerCase());
		hover.put("value", value);
		jsonObject.put("hoverEvent", hover);
		return this;
	}



	public static IChatBaseComponent fromString(String json) {
		try {
			if (json == null) return null;
			JSONObject jsonObject = ((JSONObject) new JSONParser().parse(json));
			IChatBaseComponent component = new IChatBaseComponent();
			if (jsonObject.containsKey("text")) component.setText((String) jsonObject.get("text"));
			if (jsonObject.containsKey("bold")) component.setBold((Boolean) jsonObject.get("bold"));
			if (jsonObject.containsKey("italic")) component.setItalic((Boolean) jsonObject.get("italic"));
			if (jsonObject.containsKey("underlined")) component.setUnderlined((Boolean) jsonObject.get("underlined"));
			if (jsonObject.containsKey("strikethrough")) component.setStrikethrough((Boolean) jsonObject.get("strikethrough"));
			if (jsonObject.containsKey("obfuscated")) component.setObfuscated((Boolean) jsonObject.get("obfuscated"));
			if (jsonObject.containsKey("color")) component.setColor(EnumChatFormat.valueOf(((String) jsonObject.get("color")).toUpperCase()));
			if (jsonObject.containsKey("clickEvent")) {
				JSONObject clickEvent = (JSONObject) jsonObject.get("clickEvent");
				String action = (String) clickEvent.get("action");
				Object value = (Object) clickEvent.get("value");
				component.onClick(ClickAction.valueOf(action.toUpperCase()), value);
			}
			if (jsonObject.containsKey("hoverEvent")) {
				JSONObject hoverEvent = (JSONObject) jsonObject.get("hoverEvent");
				String action = (String) hoverEvent.get("action");
				String value = (String) hoverEvent.get("value");
				component.onHover(HoverAction.valueOf(action.toUpperCase()), value);
			}
			if (jsonObject.containsKey("extra")) {
				List<JSONObject> list = (List<JSONObject>) jsonObject.get("extra");
				for (JSONObject extra : list) {
					component.addExtra(fromString(extra.toString()));
				}
			}
			if (jsonObject.containsKey("signature")) {
				component.signature = (String) jsonObject.get("signature");
				component.jsonObject.put("signature", component.signature);
			}
			return component;
		} catch (ParseException | ClassCastException e) {
			return fromColoredText(json);
		}
	}
	public String toString() {
		if (extra == null) {
			if (text == null) return null;
			if (text.length() == 0) return EMPTY_SIGNED_COMPONENT;
		}
		jsonObject.put("signature", "TAB");
		if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 7) {
			//1.7+
			return jsonObject.toString();
		} else {
			String text = toColoredText();
			if (ProtocolVersion.SERVER_VERSION.getMinorVersion() == 6) {
				//1.6.x
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("text", text);
				return jsonObject.toString();
			} else {
				//1.5.x
				return text;
			}
		}
	}

	public static IChatBaseComponent fromColoredText(String message){
		if (message == null) return null;
		List<IChatBaseComponent> components = new ArrayList<IChatBaseComponent>();
		StringBuilder builder = new StringBuilder();
		IChatBaseComponent component = new IChatBaseComponent();
		for (int i = 0; i < message.length(); i++){
			char c = message.charAt(i);
			if (c == Placeholders.colorChar || c == '&'){
				i++;
				if (i >= message.length()) {
					break;
				}
				c = message.charAt(i);
				if ((c >= 'A') && (c <= 'Z')) {
					c = (char)(c + ' ');
				}
				EnumChatFormat format = EnumChatFormat.getByChar(c);
				if (format != null){
					if (builder.length() > 0){
						component.setText(builder.toString());
						components.add(component);
						component = new IChatBaseComponent();
						builder = new StringBuilder();
					}
					switch (format){
					case BOLD: 
						component.setBold(true);
						break;
					case ITALIC: 
						component.setItalic(true);
						break;
					case UNDERLINE: 
						component.setUnderlined(true);
						break;
					case STRIKETHROUGH: 
						component.setStrikethrough(true);
						break;
					case OBFUSCATED: 
						component.setObfuscated(true);
						break;
					case RESET: 
						format = EnumChatFormat.WHITE;
					default: 
						component = new IChatBaseComponent();
						component.setColor(format);
						break;
					}
				}
			} else {
				int pos = message.indexOf(' ', i);
				if (pos == -1) {
					pos = message.length();
				}
				builder.append(c);
			}
		}
		component.setText(builder.toString());
		components.add(component);

		return new IChatBaseComponent("").setExtra(components);
	}
	public String toColoredText() {
		StringBuilder builder = new StringBuilder();
		if (color != null) builder.append(color.getFormat());
		if (isBold()) builder.append(EnumChatFormat.BOLD.getFormat());
		if (isItalic()) builder.append(EnumChatFormat.ITALIC.getFormat());
		if (isUnderlined()) builder.append(EnumChatFormat.UNDERLINE.getFormat());
		if (isStrikethrough()) builder.append(EnumChatFormat.STRIKETHROUGH.getFormat());
		if (isObfuscated()) builder.append(EnumChatFormat.OBFUSCATED.getFormat());
		if (text != null) builder.append(text);
		if (extra != null) {
			for (IChatBaseComponent component : extra) {
				builder.append(component.toColoredText());
			}
		}
		return builder.toString();
	}

	public enum ClickAction{
		OPEN_URL,
		@Deprecated OPEN_FILE,//Cannot be sent by server
		RUN_COMMAND,
		@Deprecated TWITCH_USER_INFO, //Removed in 1.9
		CHANGE_PAGE,
		SUGGEST_COMMAND,
		COPY_TO_CLIPBOARD; //since 1.15
	}
	public enum HoverAction{
		SHOW_TEXT,
		SHOW_ITEM,
		SHOW_ENTITY,
		@Deprecated SHOW_ACHIEVEMENT;//Removed in 1.12
	}
}