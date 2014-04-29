#include "NBT.h"

/*
This is a C++ implementation of the Named Binary Tag (NBT) format 
invented by Markus Persson and released under the open-source BSD
license.
*/

namespace NBT{
	// private headers
	bool writeDataContent(uint8_t tagType, void* data, int32_t dataSize, std::ostream& output);


	// maker functions
	NBTtag makeByteTag(const char* name, uint8_t* data){
		NBTtag newTag;
		newTag.TYPE = TYPE_BYTE;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = 1;
		return newTag;
	}
	NBTtag makeShortTag(const char* name, int16_t* data){
		NBTtag newTag;
		newTag.TYPE = TYPE_SHORT;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = 2;
		return newTag;
	}
	NBTtag makeIntTag(const char* name, int32_t* data){
		NBTtag newTag;
		newTag.TYPE = TYPE_INT;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = 4;
		return newTag;
	}
	NBTtag makeLongTag(const char* name, int64_t* data){
		NBTtag newTag;
		newTag.TYPE = TYPE_LONG;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = 8;
		return newTag;
	}
	NBTtag makeFloatTag(const char* name, float* data){
		NBTtag newTag;
		newTag.TYPE = TYPE_FLOAT;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = 4;
		return newTag;
	}
	NBTtag makeDoubleTag(const char* name, double* data){
		NBTtag newTag;
		newTag.TYPE = TYPE_DOUBLE;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = 8;
		return newTag;
	}
	NBTtag makeByteArrayTag(const char* name, uint8_t* data, int32_t size){
		NBTtag newTag;
		newTag.TYPE = TYPE_BYTE_ARRAY;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = size;
		return newTag;
	}
	NBTtag makeStringTag(const char* name, const char* data){
		int16_t length = 0;
		while (data[length] != '\0'){
			length++;
		}
		// do not inlude null terminator!
		NBTtag newTag;
		newTag.TYPE = TYPE_STRING;
		newTag.NAME = name;
		newTag.DATA = &data;
		newTag.ARRAY_LENGTH = length;
		return newTag;
	}
	NBTtag makeListTag(const char* name, NBTtag* data, int32_t size){
		NBTtag newTag;
		newTag.TYPE = TYPE_LIST;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = size;
		return newTag;
	}
	NBTtag makeCompoundTag(const char* name, NBTtag* data, int32_t size){
		NBTtag newTag;
		newTag.TYPE = TYPE_COMPOUND;
		newTag.NAME = name;
		newTag.DATA = data;
		newTag.ARRAY_LENGTH = size ;
		return newTag;
	}

	// writer functions
	bool writeTag(NBTtag& tag, std::ostream& output){
		bool success = true;
		uint8_t tagType = tag.TYPE;
		int16_t nameLength = 0;
		while (tag.NAME[nameLength] != '\0'){
			nameLength++;
		}
		// do not inlude null terminator!
		// change to big-endian
		UINT16 be_nameLength = htons(nameLength);
		output.write(reinterpret_cast<const char*>(&tagType), 1);
		output.write(reinterpret_cast<const char*>(&be_nameLength), 2);
		output.write(reinterpret_cast<const char*>(tag.NAME), nameLength);
		
		success = success && writeDataContent(tagType, tag.DATA, tag.ARRAY_LENGTH, output);
			
		return success;
	}

	

	bool writeDataContent(uint8_t tagType, void* data, int32_t dataSize, std::ostream& output){
		switch (tagType){
		case TYPE_BYTE:{
						   output.write(reinterpret_cast<const char*>(data), 1);
						   break; }
		case TYPE_SHORT:{
							// big-endian conversion
							UINT16 be_data = htons(*reinterpret_cast<UINT16*>(data));
							output.write(reinterpret_cast<const char*>(&be_data), 2);
							break;
		}
		case TYPE_INT:{
						  // big-endian conversion
						  UINT32 be_data = htonl(*reinterpret_cast<UINT32*>(data));
						  output.write(reinterpret_cast<const char*>(&be_data), 4);
						  break;
		}
		case TYPE_LONG:{
						   // big-endian conversion
						   UINT64 be_data = htonll(*reinterpret_cast<UINT64*>(data));
						   output.write(reinterpret_cast<const char*>(&be_data), 8);
						   break;
		}
		case TYPE_FLOAT:{
							// big-endian conversion
							UINT32 be_data = htonl(*reinterpret_cast<UINT32*>(data)); // a float is 32 bits
							output.write(reinterpret_cast<const char*>(&be_data), 4);
							break;
		}
		case TYPE_DOUBLE:{
			// big-endian conversion
			UINT64 be_data = htonll(*reinterpret_cast<UINT64*>(data)); // a double is 64 bits
			output.write(reinterpret_cast<const char*>(&be_data), 8);
			break;
		}
		case TYPE_BYTE_ARRAY:{
			UINT32 balength = htonl(dataSize);
			output.write(reinterpret_cast<const char*>(&balength), 4);
			output.write(reinterpret_cast<const char*>(data), dataSize); // endian conversion not necessary for byte arrays
		}
			break;
		case TYPE_STRING:{
							 int16_t slength = htons(static_cast<int16_t>(dataSize)); // endian conversion
							 output.write(reinterpret_cast<const char*>(&slength), 2);
							 const char** str = reinterpret_cast<const char**>(data);
							 output.write(*str, dataSize); }
			break;
		case TYPE_LIST:{
						   NBTtag *data_li = static_cast<NBTtag*>(data);
						   int32_t be_length = htonl(dataSize); // endian conversion
						   if (dataSize > 0){
							   uint8_t typeByte = data_li[0].TYPE;
							   output.write(reinterpret_cast<const char*>(&typeByte), 1);
							   output.write(reinterpret_cast<const char*>(&be_length), 4);
							   for (int i = 0; i < dataSize; i++){
								   writeDataContent(typeByte, data_li[i].DATA, data_li[i].ARRAY_LENGTH, output);
							   }
						   }
		}
			break;
		case TYPE_COMPOUND:{
							   NBTtag* data_cmp_ptr = static_cast<NBTtag*>(data);
							   for (int i = 0; i < dataSize; i++){
								   writeTag(data_cmp_ptr[i], output);
							   }
							   output.write(reinterpret_cast<const char*>(&TYPE_END), 1); }
			break;
		default:
			return false;
		}
		return true;
	}
} // NBT namespace