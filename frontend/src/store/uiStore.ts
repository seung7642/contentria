import { create } from 'zustand';

interface UiState {
  isSubscribeModalOpen: boolean;
  openSubscribeModal: () => void;
  closeSubscribeModal: () => void;
}

export const useUiStore = create<UiState>((set) => ({
  isSubscribeModalOpen: false,
  openSubscribeModal: () => set({ isSubscribeModalOpen: true }),
  closeSubscribeModal: () => set({ isSubscribeModalOpen: false }),
}));
