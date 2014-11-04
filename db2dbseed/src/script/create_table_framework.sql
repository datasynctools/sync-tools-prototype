create table seed.SyncEntity (
	EntityId					varchar(36)		not null,
	EntityName					varchar(50)		not null,
	SyncPeerLeft				varchar(36)		not null,
	SyncPeerRight				varchar(36)		not null,
	ProcessingOrderAddUpdate	integer			not null,
	ProcessingOrderDelete		integer			not null,
	primary key (EntityId)
);

create table seed.SyncPeer (
	PeerId			varchar(36)		not null,
	PeerName		varchar(50)		not null,
	primary key (PeerId)
);

create table seed.SyncState (
	EntityId		varchar(36)		not null,
	RecordId		varchar(36)		not null,
	RecordHash		varchar(100)	not null,
	RecordData		clob			not null,
);
