CREATE SCHEMA seed AUTHORIZATION DBA;

create table seed.SyncPeer (
	PeerId			varchar(36)		not null,
	PeerName		varchar(50)		not null,
	primary key (PeerId)
);

create table seed.SyncEntity (
	EntityId					varchar(36)		not null,
	EntityName					varchar(50)		not null,
	SyncPeerThis				varchar(36)		not null,
	SyncPeerRemote				varchar(36)		not null,
	ProcessingOrderAddUpdate	integer			not null,
	ProcessingOrderDelete		integer			not null,
	primary key (EntityId),
	FOREIGN KEY (SyncPeerThis) REFERENCES seed.SyncPeer(PeerId),
	FOREIGN KEY (SyncPeerRemote) REFERENCES seed.SyncPeer(PeerId)
);

create table seed.SyncState (
	EntityId		varchar(36)		not null,
	RecordId		varchar(112)		not null,
	RecordHash		varchar(100)	not null,
	RecordData		varchar(2000)	not null
--FOREIGN KEY (EntityId) REFERENCES seed.SyncEntity(EntityId)
);
