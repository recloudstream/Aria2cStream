How it works:
Aria2c is a c++ downloader, and that means that we cant communicate with it directly. So this project
starts a aria2c server and then uses a websocket rpc to send and poll all data.

Aria2c uses gid to represent an id to every file that you download, however to be able to store a
persistent key to a download we use an internal id that has a one to one mapping to the head gid,
where a head gid has information of all gids itself has downloaded as torrents can have many downloads.
DownloadListener has all the mappings between gid and id, and we observe the update count to update
all views, while we use getInfo(gid) to get all the metadata about a single download.

All downloads are made with a UriRequest and that contains all Aria2c options. Be aware that because
it was built in c++ file operations does not use the android new file api, so this is useless for
direct download above api 29 (aka no legacy file storage) and files will have to be copied over when
done. This project has no such functionally atm and the user has to do that.

