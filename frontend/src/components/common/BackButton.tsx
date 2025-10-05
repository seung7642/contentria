import { ArrowLeft } from 'lucide-react';

interface BackButtonProps {
  onClick: () => void;
  className?: string;
  ariaLabel?: string;
}

const BackButton: React.FC<BackButtonProps> = ({
  onClick,
  className = 'absolute left-4 top-4 text-gray-500 hover:text-gray-700',
  ariaLabel = 'Go back',
}) => {
  return (
    <button type="button" onClick={onClick} className={className} aria-label={ariaLabel}>
      <ArrowLeft size={20} />
    </button>
  );
};

export default BackButton;
