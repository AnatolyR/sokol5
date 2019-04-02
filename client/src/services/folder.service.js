import axios from 'axios';

export const folderService = {
    getFolderMetadata(folderId) {
        return axios.get(`/folders/${folderId}/metadata`);
    },

    getFolderData(folderId, query) {
        return axios.get(`/api/folders/${folderId}/data`);
    }
};