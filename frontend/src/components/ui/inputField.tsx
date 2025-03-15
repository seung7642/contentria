import React from 'react';

type InputFieldProps = {
  id: string;
  name: string;
  type: string;
  icon: React.ElementType;
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  autoComplete?: string;
  isRounded?: 'top' | 'bottom' | 'both' | 'none';
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
      <label htmlFor={id} className="sr-only">
        {placeholder}
      </label>
      <div className="relative">
        <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
          <Icon className="h-5 w-5 text-gray-400" />
        </div>
        <input
          id={id}
          name={name}
          type={type}
          autoComplete={autoComplete}
          required
          className={`relative block w-full appearance-none border border-gray-300 px-3 py-2 pl-10 text-gray-900 placeholder-gray-500 focus:z-10 focus:border-indigo-500 focus:outline-none focus:ring-indigo-500 sm:text-sm ${getRoundedClass()}`}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          disabled
        />
      </div>
    </div>
  );
};

export default InputField;
