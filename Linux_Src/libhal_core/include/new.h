#ifndef __NEW_H_2000_09_18_DEFINED_
#define __NEW_H_2000_09_18_DEFINED_

#include <sys/types.h>
#include <malloc.h>

#define new_free(p)					free(p)
#define new_malloc(s)				malloc(s)
#define new_realloc(ptr, size)		realloc((ptr), (size))

#ifdef _DEBUG
    extern void *DEBUG_NEW( size_t dwSize, 
			char *__file__, int __line__, char *__function__ );
    #define NEW(T, nItems)    (T *)DEBUG_NEW( sizeof(T)*(nItems), \
			(char *)__FILE__, __LINE__, (char *)__FUNCTION__)

	extern void *DEBUG_RENEW(void *pOldPtr, size_t dwNewSize, 
			char *__file__, int __line__, char *__function__ );
    #define RENEW(T, pOldPtr, nNewItems)    (T *)DEBUG_RENEW((pOldPtr), \
			sizeof(T)*(nNewItems), \
			(char *)__FILE__, __LINE__, (char *)__FUNCTION__)
    
    extern void  DEBUG_DELETE( void *p, \
			char *__file__, int __line__, char *__function__);
    #define DELETE(p)   DEBUG_DELETE((void *)(p), \
			(char *)__FILE__, __LINE__, (char *)__FUNCTION__ )

	extern int _MEM_MGR_SHOW_NEW_MSG;	// to show each DELETE/NEW msg
	#define MEM_MGR_SHOW_MSG(bShow)		(_MEM_MGR_SHOW_NEW_MSG=(bShow))

	extern int MEM_GET_INFO(int *pnAllocatedMemoryId,
		size_t *pulMemoryByteCount);
#else
    #define NEW(T, nItems)					\
			(T *)new_malloc( sizeof(T)*(nItems) )

	// To read the following comments carefully before using RENEW.
    #define RENEW(T, pOldPtr, nNewItems)    \
			(T *)new_realloc((pOldPtr), sizeof(T)*(nNewItems))
    #define DELETE(p)						new_free(p)

	#define MEM_MGR_SHOW_MSG(bShow)		(void)(bShow)
	#define MEM_GET_INFO(pId, pCount)	(0) 

#endif

#define SAFELY_DELETE(p)	\
		do { if ((p) != NULL) { DELETE(p); (p) = NULL; } } while(0)

//
//comments about realloc() from .NET 2003.
/*
Reallocate memory blocks.

void *realloc(
   void *memblock,
   size_t size 
);

Parameters:
	memblock : Pointer to previously allocated memory block. 
	size : New size in bytes. 

Return Value:
	realloc returns a void pointer to the reallocated (and possibly moved) 
	memory block. The return value is NULL if the size is zero and the buffer
	argument is not NULL, or if there is not enough available memory to expand 
	the block to the given size. In the first case, the original block is freed.
	In the second, the original block is unchanged. The return value points to
	a storage space that is guaranteed to be suitably aligned for storage of 
	any type of object. To get a pointer to a type other than void, use a type
	cast on the return value.

Remarks
	The realloc function changes the size of an allocated memory block.
	The memblock argument points to the beginning of the memory block. 
	If memblock is NULL, realloc behaves the same way as malloc and allocates 
	a new block of size bytes. If memblock is not NULL, it should be a pointer 
	returned by a previous call to calloc, malloc, or realloc.

	The size argument gives the new size of the block, in bytes. The contents of
	the block are unchanged up to the shorter of the new and old sizes, although 
	the new block can be in a different location. Because the new block can be in
	a new memory location, the pointer returned by realloc is not guaranteed to
	be the pointer passed through the memblock argument.
*/


#endif
