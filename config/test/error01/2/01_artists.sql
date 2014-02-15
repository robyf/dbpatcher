create table ARTISTS (
	ARTIST_ID		bigint(20) not null primary key auto_increment,
	NAME			varchar(256) character set latin1 collate latin1_bin not null,
	
	unique key ARTISTS_UK1 (NAME)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
