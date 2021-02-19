#pragma version(1)
#pragma rs_fp_inprecise
#pragma rs java_package_name(com.enrique.stackblur)

static unsigned short const stackblur_mul[255] =
{
        512,512,456,512,328,456,335,512,405,328,271,456,388,335,292,512,
        454,405,364,328,298,271,496,456,420,388,360,335,312,292,273,512,
        482,454,428,405,383,364,345,328,312,298,284,271,259,496,475,456,
        437,420,404,388,374,360,347,335,323,312,302,292,282,273,265,512,
        497,482,468,454,441,428,417,405,394,383,373,364,354,345,337,328,
        320,312,305,298,291,284,278,271,265,259,507,496,485,475,465,456,
        446,437,428,420,412,404,396,388,381,374,367,360,354,347,341,335,
        329,323,318,312,307,302,297,292,287,282,278,273,269,265,261,512,
        505,497,489,482,475,468,461,454,447,441,435,428,422,417,411,405,
        399,394,389,383,378,373,368,364,359,354,350,345,341,337,332,328,
        324,320,316,312,309,305,301,298,294,291,287,284,281,278,274,271,
        268,265,262,259,257,507,501,496,491,485,480,475,470,465,460,456,
        451,446,442,437,433,428,424,420,416,412,408,404,400,396,392,388,
        385,381,377,374,370,367,363,360,357,354,350,347,344,341,338,335,
        332,329,326,323,320,318,315,312,310,307,304,302,299,297,294,292,
        289,287,285,282,280,278,275,273,271,269,267,265,263,261,259
};

static unsigned char const stackblur_shr[255] =
{
        9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16, 16, 16, 17,
        17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 18, 19,
        19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20,
        20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21,
        21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21,
        21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22,
        22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
        22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23,
        23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
        23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
        23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
        23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
        24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
        24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
        24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
        24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24
};

rs_allocation gIn;

uint32_t width;
uint32_t height;

uint32_t radius;

void __attribute__((kernel)) blur_v(uint32_t in) {
    uint x, y, xp, yp, i;
    uint sp;
    uint stack_start;
    uchar3* stack_ptr;

    uint32_t src_i;
    uint32_t dst_i;

    uint3 sum;
    uint3 sum_in;
    uint3 sum_out;
    uint3 sum_tmp;

    uchar3 inPixel;

    uint32_t wm = width - 1;
    uint32_t hm = height - 1;
    uint32_t div = (radius * 2) + 1;
    uint32_t mul_sum = stackblur_mul[radius];
    uchar shr_sum = stackblur_shr[radius];
    uchar3 stack[div];

    x = in;
    sum = sum_in = sum_out = 0;

    src_i = x; // x,0
    for(i = 0; i <= radius; i++)
    {
        stack_ptr    = &stack[i];

        inPixel = rsGetElementAt_uchar4(gIn, src_i).xyz;

        *stack_ptr = inPixel;

        sum_tmp = convert_uint3(inPixel);
        sum += sum_tmp * (i + 1);
        sum_out += sum_tmp;
    }
    for(i = 1; i <= radius; i++)
    {
        if(i <= hm) src_i += width; // +stride

        stack_ptr = &stack[i + radius];

        inPixel = rsGetElementAt_uchar4(gIn, src_i).xyz;

        *stack_ptr = inPixel;

        sum_tmp = convert_uint3(inPixel);
        sum += sum_tmp * (radius + 1 - i);
        sum_in += sum_tmp;
    }

    sp = radius;
    yp = radius;
    if (yp > hm) yp = hm;
    src_i = x + yp * width; // img.pix_ptr(x, yp);
    dst_i = x;               // img.pix_ptr(x, 0);
    for(y = 0; y < height; y++)
    {
        uchar4 outPixel;
        outPixel.xyz = convert_uchar3((sum * mul_sum) >> shr_sum);
        outPixel.w = rsGetElementAt_uchar4(gIn, dst_i).w;
        outPixel = min(max(outPixel, 0), outPixel.w);
        rsSetElementAt_uchar4(gIn, outPixel, dst_i);
        dst_i += width;

        sum -= sum_out;

        stack_start = sp + div - radius;
        if(stack_start >= div) stack_start -= div;
        stack_ptr = &stack[stack_start];

        sum_out -= convert_uint3(*stack_ptr);

        if(yp < hm)
        {
            src_i += width; // stride
            ++yp;
        }

        inPixel = rsGetElementAt_uchar4(gIn, src_i).xyz;

        *stack_ptr = inPixel;

        sum_in += convert_uint3(inPixel);

        sum += sum_in;

        ++sp;
        if (sp >= div) sp = 0;
        stack_ptr = &stack[sp];

        sum_tmp = convert_uint3(*stack_ptr);

        sum_out += sum_tmp;
        sum_in -= sum_tmp;
    }
}

void __attribute__((kernel)) blur_h(uint32_t in) {
    uint x, y, xp, yp, i;
    uint sp;
    uint stack_start;
    uchar3* stack_ptr;

    uint32_t src_i;
    uint32_t dst_i;

    uint3 sum;
    uint3 sum_in;
    uint3 sum_out;
    uint3 sum_tmp;

    uchar3 inPixel;

    uint32_t wm = width - 1;
    uint32_t hm = height - 1;
    uint32_t div = (radius * 2) + 1;
    uint32_t mul_sum = stackblur_mul[radius];
    uchar shr_sum = stackblur_shr[radius];
    uchar3 stack[div];

    y = in;
    sum = sum_in = sum_out = 0;

    src_i = width * y; // start of line (0,y)

    for(i = 0; i <= radius; i++)
    {
        stack_ptr    = &stack[ i ];
        inPixel = rsGetElementAt_uchar4(gIn, src_i).xyz;
        *stack_ptr = inPixel;
        sum_tmp = convert_uint3(inPixel);
        sum_out += sum_tmp;
        sum += sum_tmp * (i + 1);
    }


    for(i = 1; i <= radius; i++)
    {
        if (i <= wm) src_i += 1;
        stack_ptr = &stack[ (i + radius) ];
        inPixel = rsGetElementAt_uchar4(gIn, src_i).xyz;

        *stack_ptr = inPixel;
        sum_tmp = convert_uint3(inPixel);
        sum += sum_tmp * (radius + 1 - i);
        sum_in += sum_tmp;
    }

    sp = radius;
    xp = radius;
    if (xp > wm) xp = wm;
    src_i = xp + y * width; //   img.pix_ptr(xp, y);
    dst_i = y * width; // img.pix_ptr(0, y);
    for(x = 0; x < width; x++)
    {
        uchar4 outPixel;
        outPixel.xyz = convert_uchar3((sum * mul_sum) >> shr_sum);
        outPixel.w = rsGetElementAt_uchar4(gIn, dst_i).w;
        outPixel = min(max(outPixel, 0), outPixel.w);

        rsSetElementAt_uchar4(gIn, outPixel, dst_i);
        dst_i += 1;

        sum -= sum_out;

        stack_start = sp + div - radius;
        if (stack_start >= div) stack_start -= div;
        stack_ptr = &stack[stack_start];

        sum_out -= convert_uint3(*stack_ptr);

        if(xp < wm)
        {
            src_i += 1;
            ++xp;
        }

        inPixel = rsGetElementAt_uchar4(gIn, src_i).xyz;

        *stack_ptr = inPixel;
        sum_in += convert_uint3(inPixel);
        sum += sum_in;

        ++sp;
        if (sp >= div) sp = 0;
        stack_ptr = &stack[sp];

        sum_out += convert_uint3(*stack_ptr);
        sum_in -= convert_uint3(*stack_ptr);
    }

}
