import React from 'react';

type InputFieldProps = {
  id: string;
  name: string;
  type: string;
  icon?: React.ElementType;
  placeholder: string;
  value: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  autoComplete?: string;
  isRounded?: 'top' | 'bottom' | 'both' | 'none';
  label?: string;
  required?: boolean;
  disabled?: boolean;
};

export const InputField: React.FC<InputFieldProps> = ({
  id,
  name,
  type,
  icon: Icon,
  placeholder,
  value,
  onChange,
  autoComplete,
  isRounded = 'both',
  label,
  disabled = false,
}) => {
  const getRoundedClass = () => {
    switch (isRounded) {
      case 'top':
        return 'rounded-t-md';
      case 'bottom':
        return 'rounded-b-md';
      case 'both':
        return 'rounded-md';
      default:
        return '';
    }
  };

  return (
    <div>
      {label && (
        <label htmlFor={id} className="mb-1 block text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <div className="relative">
        <input
          id={id}
          name={name}
          type={type}
          autoComplete={autoComplete}
          required
          disabled={disabled}
          className={`relative block w-full appearance-none border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:z-10 focus:border-indigo-500 focus:outline-none focus:ring-indigo-500 sm:text-sm ${getRoundedClass()} ${disabled ? 'cursor-not-allowed bg-gray-50' : ''}`}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
        />
      </div>
    </div>
  );
};

export default InputField;
