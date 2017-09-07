int ring_debug=3;
union smdata
{
	int	didata;
	float	aidata;
	char	info[4];
};
typedef union smdata 	SM_DATA_TYPE;

   union smdata *write_p;
   union smdata *read_p;
   union smdata *end_p1;     /* end + 1 */
   union smdata *begin_p1;
   union smdata buffer1[68];
   union smdata *end_p2;     /* end + 1 */
   union smdata *begin_p2;
   union smdata buffer2[68];
