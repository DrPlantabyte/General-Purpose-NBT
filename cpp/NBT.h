#ifndef NBT_H
#define NBT_H

#include <iostream>
#include <stdint.h>
#include <vector>

#if defined(WIN32) || defined(_WIN32) || defined(_WIN32_) || defined(WIN64) 
#include <WinSock2.h> // for htonl() and other byte-swapping functions
#else
#include <endian.h> // for htonl() and other byte-swapping functions
#define htons(y) htobe16(y)
#define htonl(y) htobe32(y)
#define htonll(y) htobe64(y)
#endif
/*
This is a C++ implementation of the Named Binary Tag (NBT) format
invented by Markus Persson and released under the open-source BSD
license.
*/

// Tag types:
const uint8_t TYPE_END = 0;
const uint8_t TYPE_BYTE = 1;
const uint8_t TYPE_SHORT = 2;
const uint8_t TYPE_INT = 3;
const uint8_t TYPE_LONG = 4;
const uint8_t TYPE_FLOAT = 5;
const uint8_t TYPE_DOUBLE = 6;
const uint8_t TYPE_BYTE_ARRAY = 7;
const uint8_t TYPE_STRING = 8;
const uint8_t TYPE_LIST = 9;
const uint8_t TYPE_COMPOUND = 10;

namespace NBT{

	struct NBTtag{
		uint8_t TYPE;
		const char* NAME;
		void* DATA;
		int32_t ARRAY_LENGTH;
	};

	NBTtag makeByteTag(const char* name, uint8_t* data);
	NBTtag makeShortTag(const char* name, int16_t* data);
	NBTtag makeIntTag(const char* name, int32_t* data);
	NBTtag makeLongTag(const char* name, int64_t* data);
	NBTtag makeFloatTag(const char* name, float* data);
	NBTtag makeDoubleTag(const char* name, double* data);
	NBTtag makeByteArrayTag(const char* name, uint8_t* data, int32_t size);
	NBTtag makeStringTag(const char* name, const char* data);
	NBTtag makeListTag(const char* name, NBTtag* data, int32_t size);
	NBTtag makeCompoundTag(const char* name, NBTtag* data, int32_t size);
	// Writes a tag to the provided output stream, returning true
	// if the write was successful, false if not
	bool writeTag(NBTtag& tag, std::ostream& output);

} // NBT namespace
#endif