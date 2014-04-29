package hall.collin.christopher.dataformat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Named Binary Tag (NBT) implementation for general use. NBT is like XML, but 
 * optimized for storing data in a binary stream. It was most famously used in 
 * the computer game Mincraft (copyright Mojang), but is a useful format for 
 * any structured binary data.
 * @see <a href="http://www.minecraft.net/docs/NBT.txt">Online NBT specification</a>
 */
public class NBTag {
	
	
	
    private final Type type;
    private Type listType = null;
    private final String name;
    private Object value;

	

    /**
     * Enum for the tag types.
     */
    public enum Type { 
		// DO NOT CHANGE ORDER OF DECLARATION IN THIS ENUM!!!
        /** End tag signifying end of compound tag */
		TAG_End,
		/** Byte tag signifying that this tag holds a single byte of 8bit data.*/
        TAG_Byte,
		/** Short tag signifying that this tag holds a 16bit integer.*/
        TAG_Short,
		/** Integer tag signifying that this tag holds a 32bit integer.*/
        TAG_Int,
		/** Long integer tag signifying that this tag holds a 64bit integer.*/
        TAG_Long,
		/** Floating decimal tag signifying that this tag holds a 32bit decimal.*/
        TAG_Float,
		/** Double-precision floating decimal tag signifying that this tag holds 
		 * a 64bit decimal.*/
        TAG_Double,
		/** Byte array tag signifying that this tag holds an array of bytes*/
        TAG_Byte_Array,
		/** String tag signifying that this tag holds text (up to 32 thousand 
		 * UTF-8 charcters).*/
        TAG_String,
		/** List tag holds an array of tags of the same data type */
        TAG_List,
		/** Compound tag holds multiple tags of any type, referenced by their 
		 * tag name, functions as a Map&lt;String,NBTag&gt;. */
        TAG_Compound;
    }

    /**
     * Create a new TAG_Compound NBT tag.
     *
     * @param type either TAG_List or TAG_Compound
     * @param name name for the new tag or null to create an unnamed tag.
     * @param value list of tags to add to the new tag.
     */
    protected NBTag( String name, Map<String,NBTag> value) {
        this(Type.TAG_Compound, name, (Object) value);
    }

    /**
     * Create a new TAG_List with an empty list. Use {@link Tag#addTag(Tag)} to add tags later.
     *
     * @param name name for this tag or null to create an unnamed tag.
     * @param listType type of the elements in this empty list.
     */
    protected NBTag(String name, Type listType) {
        this(Type.TAG_List, name, listType);
    }

    /**
     * The preferred method of NBTag creation is to use the NBTag.make___(...) 
	 * static methods. Constructs a new NBT tag. The data type of 
	 * <code>value</code> must match the Type of this tag. List tags take a 
	 * java.util.List&lt;NBTag&gt; while compound tags take a 
	 * java.util.Map&lt;String,NBTag&gt;
     *
     * @param type any value from the {@link Type} enum.
     * @param name name for the new tag or null to create an unnamed tag.
     * @param value an object that fits the tag type or a {@link Type} to create an empty TAG_List with this list type.
     */
    public NBTag(Type type, String name, Object value) {
        switch (type) {
        case TAG_End:
            if (value != null)
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Byte:
            if (!(value instanceof Byte))
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Short:
            if (!(value instanceof Short))
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Int:
            if (!(value instanceof Integer))
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Long:
            if (!(value instanceof Long))
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Float:
            if (!(value instanceof Float))
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Double:
            if (!(value instanceof Double))
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Byte_Array:
            if (!(value instanceof byte[]))
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_String:
            if (!(value instanceof String))
                throw new IllegalArgumentException(value.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_List:
            if (value instanceof Type) {
                this.listType = (Type) value;
                value = new ArrayList<NBTag>();
            } else {
                if (!(value instanceof List))
					throw new IllegalArgumentException(Type.TAG_List.name() + " expects an object of class java.util.List<NBTag>");
                this.listType = (((List<NBTag>) value).get(0)).getType();
            }
            break;
        case TAG_Compound:
            if (!(value instanceof Map))
					throw new IllegalArgumentException(Type.TAG_Compound.name() + " expects an object of class java.util.Map<String,NBTag>");
            break;
        default:
            throw new IllegalArgumentException();
        }
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object newValue)
    {
        switch (type) {
        case TAG_End:
            if (value != null)
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Byte:
            if (!(value instanceof Byte))
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Short:
            if (!(value instanceof Short))
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Int:
            if (!(value instanceof Integer))
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Long:
            if (!(value instanceof Long))
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Float:
            if (!(value instanceof Float))
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Double:
            if (!(value instanceof Double))
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_Byte_Array:
            if (!(value instanceof byte[]))
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_String:
            if (!(value instanceof String))
                throw new IllegalArgumentException(newValue.getClass().getName() + " cannot be stored in a " + type.name());
            break;
        case TAG_List:
            if (value instanceof Type) {
                this.listType = (Type) value;
                value = new ArrayList<NBTag>();
            } else {
                if (!(value instanceof List))
					throw new IllegalArgumentException(value.getClass().getName() + " is not a List type");
                this.listType = (((List<NBTag>) value).get(0)).getType();
            }
            break;
        case TAG_Compound:
            if (!(value instanceof Map))
                throw new IllegalArgumentException(value.getClass().getName() + " is not a Map type");
            break;
        default:
                throw new IllegalArgumentException("Unrecognized NBTag type: " + type.name());
        }

        value = newValue;
    }

    public Type getListType() {
        return listType;
    }

    /**
     * Add a tag to a TAG_List or a TAG_Compound.
     */
    public void addTag(NBTag tag) {
        if (type != Type.TAG_List && type != Type.TAG_Compound)
            throw new UnsupportedOperationException("Cannot add a NBTag of type " + tag.type.name() + " to a NBTag of type " + this.type.name());
        if(type == Type.TAG_List){
			List<NBTag> subtags = (List<NBTag>) value;
			subtags.add(tag);
		} else if(type == Type.TAG_Compound){
			Map<String,NBTag> subtags = (Map<String,NBTag>) value;
			subtags.put(tag.name, tag);
		} 
		

    }

    

    /**
     * Remove a tag from a TAG_List
     *
     * @return the removed tag
     */
    public NBTag removeTag(int index) {
        if (type != Type.TAG_List)
            throw new UnsupportedOperationException("Cannot remove a NBTag by index from a NBTag of type " + this.type.name());
        
		List<NBTag> subtags = (List<NBTag>) value;
		return subtags.remove(index);
		
    }
	 /**
     * Remove a tag from a TAG_Compound
     *
     * @return the removed tag
     */
    public NBTag removeTag(String targetName) {
        if (type != Type.TAG_Compound)
            throw new UnsupportedOperationException("Cannot remove a NBTag by name from a NBTag of type " + this.type.name());

		Map<String,NBTag> subtags = (Map<String,NBTag>) value;
		return subtags.remove(targetName);
		
    }

    /**
     * Remove a tag from a TAG_List or a TAG_Compound. If the tag is not a child of this tag then nested tags are searched.
     *
     * @param tag tag to look for
     */
    public void removeSubTag(NBTag tag) {
        if (type != Type.TAG_List && type != Type.TAG_Compound)
            throw new UnsupportedOperationException("Cannot remove a NBTag from a NBTag of type " + this.type.name());
        if (tag == null)
            return;
        if(type == Type.TAG_List){
			List<NBTag> subtags = (List<NBTag>)value;
			subtags.remove(tag);
			return;
		} else if(type == Type.TAG_Compound){
			Map<String,NBTag> subtags = (Map<String,NBTag>)value;
			for(NBTag n : subtags.values()){
				if(n.type == Type.TAG_Compound){
					n.removeSubTag(tag);
				}
			}
			if(subtags.get(tag.name) == tag){
				subtags.remove(tag.name);
			}
		}
		
    }

    /**
     * Find the first nested tag with specified name in a TAG_Compound.
     *
     * @param name the name to look for. May be null to look for unnamed tags.
     * @return the first nested tag that has the specified name.
     */
    public NBTag findTagByName(String name) {
       if (type != Type.TAG_List && type != Type.TAG_Compound)
            throw new UnsupportedOperationException("NBTag of type " + this.type.name() + " does not have member NBTags");
        if (name == null)
            return null;
        if(type == Type.TAG_List){
			List<NBTag> subtags = (List<NBTag>)value;
			for(NBTag t : subtags){
				if(t.name.equals(name)){
					return t;
				}
			}
			return null;
		} else if(type == Type.TAG_Compound){
			Map<String,NBTag> subtags = (Map<String,NBTag>)value;
			if(subtags.containsKey(name)){
				return subtags.get(name);
			}
			for(NBTag c : subtags.values()){
				if(c.type == Type.TAG_Compound){
					NBTag searchResult = c.findTagByName(name);
					if(searchResult != null){
						return searchResult;
					}
				}
			}
			return null;
		}
		return null;
    }

    /**
     * Read a tag and its nested tags from an InputStream.
     *
     * @param is stream to read from, like a FileInputStream
     * @return NBT tag or structure read from the InputStream
     * @throws IOException if there was no valid NBT structure in the InputStream or if another IOException occurred.
     */
    public static NBTag readFromGZipStream(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(new GZIPInputStream(is));
        return readNBTag(dis);
    }
	/**
	 * Reads a short (2 byte) from the data stream, then reads that many UTF-8 
	 * characters and returns it as a String.
	 * @param dis
	 * @return
	 * @throws IOException 
	 */
	private static String readTagName(DataInputStream dis) throws IOException{
		short nameLength = readShort(dis);
		byte[] utfChars = new byte[nameLength];
		dis.read(utfChars);
		String name = new String(utfChars);
		return name;
	}
	/**
	 * Reads a byte from the data stream, correcting for wrong endian-ness
	 * @param dis
	 * @return
	 * @throws IOException 
	 */
	static byte readByte(DataInputStream dis) throws IOException{
		return dis.readByte();
	}
	/**
	 * Reads a short from the data stream, correcting for wrong endian-ness
	 * @param dis
	 * @return
	 * @throws IOException 
	 */
	static short readShort(DataInputStream dis) throws IOException{
		return dis.readShort();
		
	}
	/**
	 * Reads an integer from the data stream, correcting for wrong endian-ness
	 * @param dis
	 * @return
	 * @throws IOException 
	 */
	static int readInt(DataInputStream dis) throws IOException{
		
			return dis.readInt();
		
	}
	/**
	 * Reads a long integer from the data stream, correcting for wrong endian-ness
	 * @param dis
	 * @return
	 * @throws IOException 
	 */
	static long readLong(DataInputStream dis) throws IOException{
		return dis.readLong();
		
	}
	/**
	 * Reads a float from the data stream, correcting for wrong endian-ness
	 * @param dis
	 * @return
	 * @throws IOException 
	 */
	static float readFloat(DataInputStream dis) throws IOException{
		
			return dis.readFloat();
		
	}
	/**
	 * Reads a double from the data stream, correcting for wrong endian-ness
	 * @param dis
	 * @return
	 * @throws IOException 
	 */
	static double readDouble(DataInputStream dis) throws IOException{
		return dis.readDouble();
		
	}
	/**
	 * Like <code>readFromGZip(InputStream)</code>, but does not try to use GZip 
	 * decompression on the InputStream.
	 * @param dis
	 * @return
	 * @throws IOException 
	 */
	public static NBTag readNBTag(DataInputStream dis) throws IOException {
		byte type = readByte(dis);
        NBTag tag = null;

        if (type == 0) {
            tag = new NBTag(Type.TAG_End, null, null);
        } else {
			String name = readTagName(dis);
            tag = new NBTag(Type.values()[type], name, readPayload(dis,type));
        }

        return tag;
	}

    private static Object readPayload(DataInputStream dis, byte type) throws IOException {
        switch (type) {
        case 0:
            return null;
        case 1:
            return readByte(dis);
        case 2:
            return readShort(dis);
        case 3:
            return readInt(dis);
        case 4:
            return readLong(dis);
        case 5:
            return readFloat(dis);
        case 6:
            return readDouble(dis);
        case 7:
            int length = readInt(dis);
            byte[] ba = new byte[length];
            dis.readFully(ba);
            return ba;
        case 8:
            return readTagName(dis);
        case 9:
            byte lt = readByte(dis);
            int ll = readInt(dis);
            List<NBTag> lo = new ArrayList<NBTag>(ll);
            for (int i = 0; i < ll; i++) {
                lo.add(new NBTag(Type.values()[lt], null, readPayload(dis, lt)));
            }
            if (lo.isEmpty())
                return Type.values()[lt];
            else
                return lo;
        case 10:
			Map<String,NBTag> children = new LinkedHashMap<>();
            NBTag content = null; 
			do{
				content = readNBTag(dis);
				if(content.type != Type.TAG_End){
					children.put(content.name,content);
				}
			} while(content.type != Type.TAG_End);
            return children;
        }
        return null;
    }

    /**
     * Read a tag and its nested tags from an InputStream.
	 * 
	 * <b>THIS METHOD USES GZIP COMPRESSION ON THE STREAM!</b>
     *
     * @param os stream to write to, like a FileOutputStream
     * @throws IOException if this is not a valid NBT structure or if any IOException occurred.
     */
    public void writeToGZip(OutputStream os) throws IOException {
        GZIPOutputStream gzos;
        DataOutputStream dos = new DataOutputStream(gzos = new GZIPOutputStream(os));
        dos.writeByte(type.ordinal());
        if (type != Type.TAG_End) {
            dos.writeUTF(name);
            writePayload(dos);
        }
        gzos.flush();
    }
	/**
	 * Turns this tag into a stream of bytes and writes it to the OutputStream 
	 * <b>without compression</b>. 
	 * @param os An output stream to write to (e.g. <br/>
	 * <code>ByteArrayOutputStream buffer = new ByteArrayOutputStream(8096); <br/>
	 * OutputStream output = new DeflaterOutputStream(buffer); <br/>
	 * tag.serialize(output)</code>).
	 * @throws IOException Thrown if there is a problem writing to the buffer.
	 */
	public void write(OutputStream os) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeByte(type.ordinal());
        if (type != Type.TAG_End) {
            dos.writeUTF(name);
            writePayload(dos);
        }
		dos.flush();
	}

    private void writePayload(DataOutputStream dos) throws IOException {
        switch (type) {
        case TAG_End:
            break;
        case TAG_Byte:
            dos.writeByte((Byte) value);
            break;
        case TAG_Short:
            dos.writeShort((Short) value);
            break;
        case TAG_Int:
            dos.writeInt((Integer) value);
            break;
        case TAG_Long:
            dos.writeLong((Long) value);
            break;
        case TAG_Float:
            dos.writeFloat((Float) value);
            break;
        case TAG_Double:
            dos.writeDouble((Double) value);
            break;
        case TAG_Byte_Array:
            byte[] ba = (byte[]) value;
            dos.writeInt(ba.length);
            dos.write(ba);
            break;
        case TAG_String:
            dos.writeUTF((String) value);
            break;
        case TAG_List:
            List<NBTag> list = (List<NBTag>) value;
            dos.writeByte(getListType().ordinal());
            dos.writeInt(list.size());
            for (NBTag tt : list) {
                tt.writePayload(dos);
            }
            break;
        case TAG_Compound:
            Map<String,NBTag> subtags = (Map<String,NBTag>) value;
            for (String n : subtags.keySet()) {
                NBTag subtag = subtags.get(n);
                Type type = subtag.getType();
                dos.writeByte(type.ordinal());
                if (type != Type.TAG_End) {
                    dos.writeUTF(subtag.getName());
                    subtag.writePayload(dos);
                }
            }
			dos.writeByte(0);// TAG_End
            break;
        }
    }

  
   
    

	
	private static void tagToString(NBTag t, StringBuilder sb, int indent) {
		if(t.getType() == Type.TAG_End) {
			return;
		}
		String tagName = t.getName();
		if(tagName == null || tagName.length() == 0){
			tagName = "TAG";
		}
		for (int i = 0; i < indent; i++) {
			sb.append("\t");
		}
		sb.append("<");
		sb.append(tagName);
		
		Object value = t.getValue();
		if(t.type == Type.TAG_List){
			sb.append(" type=\"list");
			if(value instanceof List){
				List<NBTag> listValue = (List<NBTag> )value;
				if(listValue.size() > 0){
					sb.append(":");
					sb.append(listValue.get(0).listType.name());
				}
			}
			sb.append("\">");
		} else if(t.type == Type.TAG_Compound) {
			sb.append(" type=\"compound tag\">");
		} else if(value instanceof byte[]) {
			sb.append(" type=\"byte[");
			sb.append(((byte[])value).length);
			sb.append("]\"/>");
		} else {
			sb.append(" type=\"");
			sb.append(value.getClass().getSimpleName());
			sb.append("\"");
			sb.append(" value=\"");
			sb.append(value);
			sb.append("\"/>");
		}
		sb.append("\n");
		
		if (value instanceof List) {
			List<NBTag> a = (List<NBTag>) value;
			for (NBTag child : a) {
				tagToString(child, sb, indent + 1);
			}
			for (int i = 0; i < indent; i++) sb.append("\t");
			sb.append("</");
			sb.append(tagName);
			sb.append(">\n");
		} else if (value instanceof Map) {
			Map<String,NBTag> a = (Map<String,NBTag>) value;
			for (String keyName : a.keySet()) {
				tagToString(a.get(keyName), sb, indent + 1);
			}
			for (int i = 0; i < indent; i++) sb.append("\t");
			sb.append("</");
			sb.append(tagName);
			sb.append(">\n");
		}
	}
	@Override
	public String toString(){
		StringBuilder out = new StringBuilder();
		tagToString(this,out,0);
		return out.toString();
	}
	/**
	 * Turns the list of provided tags into a compound tag that holds them. The 
	 * TAG_END tag is appended to the end, so it should not be provided as one 
	 * of the arguments
	 * @param name Name of the tag
	 * @param childTags The child tags, <b>not including the closing TAG_End</b>
	 * @return A compound tag whose contents are <code>childTags</code> plus 
	 * an END_TAG
	 */
	public static NBTag makeCompoundTag(String name, NBTag... childTags){
		Map<String,NBTag> map = new LinkedHashMap<>();
		for(NBTag tag : childTags){
			map.put(tag.getName(), tag);
		}
		return new NBTag(name,map);
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of tags to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, NBTag... data){
		Type t = data[0].type;
		NBTag tag = new NBTag(name,t);
		for(NBTag e : data){
			if(e.type != t){
				throw new IllegalArgumentException("Tag type " + e.type.name() + " cannot be added to list of "+t.name()+" tags.");
			}
			tag.addTag(e);
		}
		return tag;
	}
	
	
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of data elements to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, Byte... data){
		NBTag tag = new NBTag(name,Type.TAG_Byte);
		for(Byte b : data){
			tag.addTag(new NBTag(Type.TAG_Byte,null,b));
		}
		return tag;
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of data elements to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, byte[]... data){
		NBTag tag = new NBTag(name,Type.TAG_Byte_Array);
		for(byte[] e : data){
			tag.addTag(new NBTag(Type.TAG_Byte_Array,null,e));
		}
		return tag;
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of list tags to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListOfListTags(String name, NBTag... data){
		NBTag tag = new NBTag(name,Type.TAG_List);
		for(NBTag e : data){
			if(e.type != Type.TAG_List){
				throw new IllegalArgumentException("Tag type " + e.type.name() + " cannot be added to list of list tags.");
			}
			tag.addTag(e);
		}
		return tag;
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of data elements to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, double... data){
		Type t = Type.TAG_Double;
		NBTag tag = new NBTag(name,t);
		for(double e : data){
			tag.addTag(new NBTag(t,null,e));
		}
		return tag;
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of data elements to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, float... data){
		Type t = Type.TAG_Float;
		NBTag tag = new NBTag(name,t);
		for(float e : data){
			tag.addTag(new NBTag(t,null,e));
		}
		return tag;
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of data elements to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, int... data){
		Type t = Type.TAG_Int;
		NBTag tag = new NBTag(name,t);
		for(int e : data){
			tag.addTag(new NBTag(t,null,e));
		}
		return tag;
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of data elements to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, long... data){
		Type t = Type.TAG_Long;
		NBTag tag = new NBTag(name,t);
		for(long e : data){
			tag.addTag(new NBTag(t,null,e));
		}
		return tag;
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of data elements to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, short... data){
		Type t = Type.TAG_Short;
		NBTag tag = new NBTag(name,t);
		for(short e : data){
			tag.addTag(new NBTag(t,null,e));
		}
		return tag;
	}
	/**
	 * Creates a list tag from the provided array of elements.
	 * @param name The name of the list tag
	 * @param data The array of data elements to store in the list tag.
	 * @return The constructed list tag.
	 */
	public static NBTag makeListTag(String name, String... data){
		Type t = Type.TAG_String;
		NBTag tag = new NBTag(name,t);
		for(String e : data){
			tag.addTag(new NBTag(t,null,e));
		}
		return tag;
	}
	/**
	 * Creates a tag to hold the given data,
	 * @param name The tag name (will be used to reference this data if this tag 
	 * is stored in a compount tag).
	 * @param data The data held by this tag.
	 * @return A new byte tag.
	 */
	public NBTag makeTag(String name, byte data){
		return new NBTag(Type.TAG_Byte,name,data);
	}
	/**
	 * Creates a tag to hold the given data,
	 * @param name The tag name (will be used to reference this data if this tag 
	 * is stored in a compount tag).
	 * @param data The data held by this tag.
	 * @return A new short tag.
	 */
	public NBTag makeTag(String name, short data){
		return new NBTag(Type.TAG_Short,name,data);
	}
	/**
	 * Creates a tag to hold the given data,
	 * @param name The tag name (will be used to reference this data if this tag 
	 * is stored in a compount tag).
	 * @param data The data held by this tag.
	 * @return A new int tag.
	 */
	public NBTag makeTag(String name, int data){
		return new NBTag(Type.TAG_Int,name,data);
	}
	/**
	 * Creates a tag to hold the given data,
	 * @param name The tag name (will be used to reference this data if this tag 
	 * is stored in a compount tag).
	 * @param data The data held by this tag.
	 * @return A new long tag.
	 */
	public NBTag makeTag(String name, long data){
		return new NBTag(Type.TAG_Long,name,data);
	}
	/**
	 * Creates a tag to hold the given data,
	 * @param name The tag name (will be used to reference this data if this tag 
	 * is stored in a compount tag).
	 * @param data The data held by this tag.
	 * @return A new float tag.
	 */
	public NBTag makeTag(String name, float data){
		return new NBTag(Type.TAG_Float,name,data);
	}
	/**
	 * Creates a tag to hold the given data,
	 * @param name The tag name (will be used to reference this data if this tag 
	 * is stored in a compount tag).
	 * @param data The data held by this tag.
	 * @return A new double tag.
	 */
	public NBTag makeTag(String name, double data){
		return new NBTag(Type.TAG_Double,name,data);
	}
	/**
	 * Creates a tag to hold the given data,
	 * @param name The tag name (will be used to reference this data if this tag 
	 * is stored in a compount tag).
	 * @param data The data held by this tag.
	 * @return A new byte array tag.
	 */
	public NBTag makeTag(String name, byte[] data){
		return new NBTag(Type.TAG_Byte_Array,name,data);
	}
	/**
	 * Creates a tag to hold the given data,
	 * @param name The tag name (will be used to reference this data if this tag 
	 * is stored in a compount tag).
	 * @param data The data held by this tag.
	 * @return A new String tag.
	 */
	public NBTag makeTag(String name, String data){
		return new NBTag(Type.TAG_String,name,data);
	}
}
